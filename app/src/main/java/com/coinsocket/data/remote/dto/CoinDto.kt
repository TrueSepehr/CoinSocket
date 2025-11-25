package com.coinsocket.data.remote.dto

import com.coinsocket.domain.model.Coin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    @SerialName("s") val symbol: String,
    @SerialName("c") val price: String
)

fun CoinDto.toCoin(): Coin {
    return Coin(
        symbol = this.symbol,
        price = this.price.toDoubleOrNull() ?: 0.0
    )
}