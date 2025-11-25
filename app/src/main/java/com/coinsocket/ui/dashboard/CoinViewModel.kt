package com.coinsocket.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinsocket.domain.usecase.GetCoinPricesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val getCoinPricesUseCase: GetCoinPricesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoinUiState>(CoinUiState.Loading)
    val uiState: StateFlow<CoinUiState> = _uiState.asStateFlow()

    init {
        subscribeToCoins()
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