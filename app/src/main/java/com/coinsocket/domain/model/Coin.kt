package com.coinsocket.domain.model

data class Coin(
    val symbol: String,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)