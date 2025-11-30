package com.coinsocket.domain.model

data class Coin(
    val symbol: String,
    val price: Double,
    val high24h: Double,
    val low24h: Double,
    val openPrice: Double
) {
    val changePercent: Double
        get() = if (openPrice == 0.0) 0.0 else ((price - openPrice) / openPrice) * 100
}