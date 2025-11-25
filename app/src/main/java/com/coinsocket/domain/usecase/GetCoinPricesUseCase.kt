package com.coinsocket.domain.usecase

import com.coinsocket.domain.model.Coin
import com.coinsocket.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCoinPricesUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<List<Coin>> {
        return repository.observeCoinPrices()
            .map { list -> list.sortedBy { it.symbol } }
    }
}