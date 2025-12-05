package com.coinsocket.domain.model

data class Coin(
    val symbol: String,
    val price: Double,
    val high24h: Double,
    val low24h: Double,
    val changePercent: Double,
    val priceHistory: List<Double> = emptyList(),
)