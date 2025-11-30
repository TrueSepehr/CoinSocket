package com.coinsocket.data.remote.dto

import com.coinsocket.domain.model.Coin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    @SerialName("s") val symbol: String,
    @SerialName("c") val price: String,
    @SerialName("h") val high: String,
    @SerialName("l") val low: String,
    @SerialName("o") val open: String
)

fun CoinDto.toCoin(): Coin {
    return Coin(
        symbol = this.symbol,
        price = this.price.toDoubleOrNull() ?: 0.0,
        high24h = this.high.toDoubleOrNull() ?: 0.0,
        low24h = this.low.toDoubleOrNull() ?: 0.0,
        openPrice = this.open.toDoubleOrNull() ?: 0.0
    )
}