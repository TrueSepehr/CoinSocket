package com.coinsocket.domain.repository

import com.coinsocket.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun observeCoinPrices(): Flow<List<Coin>>

    suspend fun connect()
    suspend fun disconnect()
}