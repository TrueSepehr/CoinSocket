package com.coinsocket.ui.dashboard

import com.coinsocket.domain.model.Coin

sealed interface CoinUiState {
    data object Loading : CoinUiState
    data class Success(val coins: List<Coin>) : CoinUiState
    data class Error(val message: String) : CoinUiState
}