package com.coinsocket.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinsocket.domain.usecase.GetCoinPricesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class SortOption {
    Name, Price, Change
}

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val getCoinPricesUseCase: GetCoinPricesUseCase
) : ViewModel() {

    private val _sortOption = MutableStateFlow(SortOption.Name)
    private val _uiState = MutableStateFlow<CoinUiState>(CoinUiState.Loading)
    val uiState: StateFlow<CoinUiState> = combine(
        getCoinPricesUseCase(),
        _sortOption
    ) { coins, sortOption ->
        val sortedList = when (sortOption) {
            SortOption.Name -> coins.sortedBy { it.symbol }
            SortOption.Price -> coins.sortedByDescending { it.price }
            SortOption.Change -> coins.sortedByDescending { it.changePercent }
        }
        CoinUiState.Success(sortedList) as CoinUiState
    }
        .onStart { emit(CoinUiState.Loading) }
        .catch { emit(CoinUiState.Error(it.message ?: "Error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoinUiState.Loading)
    init {
        subscribeToCoins()
    }

    fun onSortChange(option: SortOption) {
        _sortOption.value = option
    }

    private fun subscribeToCoins() {
        getCoinPricesUseCase()
            .onStart {
                _uiState.value = CoinUiState.Loading
            }
            .onEach { coins ->
                _uiState.value = CoinUiState.Success(coins)
            }
            .catch { e ->
                _uiState.value = CoinUiState.Error(e.message ?: "Unknown Error")
            }
            .launchIn(viewModelScope)
    }
}