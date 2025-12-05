package com.coinsocket.data.repository

import com.coinsocket.data.remote.dto.CoinDto
import com.coinsocket.data.remote.dto.parseKlines
import com.coinsocket.domain.model.Coin
import com.coinsocket.domain.repository.CoinRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : CoinRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val popularCoins = setOf(
        "BTCUSDT",  // Bitcoin
        "ETHUSDT",  // Ethereum
        "BNBUSDT",  // Binance Coin
        "SOLUSDT",  // Solana
        "XRPUSDT",  // Ripple
        "ADAUSDT",  // Cardano
        "DOGEUSDT", // Dogecoin
        "DOTUSDT",  // Polkadot
        "TRXUSDT",  // Tron
        "LTCUSDT"   // Litecoin
    )

    private suspend fun fetchHistoricalPrices(symbol: String): List<Double> {
        return try {
            val response = client.get("https://api.binance.com/api/v3/uiKlines") {
                url {
                    parameters.append("symbol", symbol)
                    parameters.append("interval", "1h")
                    parameters.append("limit", "24")
                }
            }
            val jsonArray = jsonParser.decodeFromString<JsonArray>(response.bodyAsText())
            parseKlines(jsonArray)
        } catch (e: Exception) {
            e.printStackTrace()
            List(20) { 0.0 }
        }
    }

    override fun observeCoinPrices(): Flow<List<Coin>> = flow {
        val initialCoinsMap = coroutineScope {
            popularCoins.map { symbol ->
                async {
                    val history = fetchHistoricalPrices(symbol)
                    val currentPrice = history.lastOrNull() ?: 0.0

                    symbol to Coin(
                        symbol = symbol,
                        price = currentPrice,
                        high24h = 0.0,
                        low24h = 0.0,
                        changePercent = 0.0,
                        priceHistory = history
                    )
                }
            }.awaitAll().toMap().toMutableMap()
        }

        emit(initialCoinsMap.values.toList())

        try {
            client.webSocket(urlString = "wss://stream.binance.com:9443/ws/!miniTicker@arr") {
                while (isActive) {
                    val frame = incoming.receive()
                    if (frame is Frame.Text) {
                        try {
                            val jsonString = frame.readText()
                            val dtos = jsonParser.decodeFromString<List<CoinDto>>(jsonString)
                            var hasChanges = false

                            dtos.forEach { dto ->
                                if (popularCoins.contains(dto.symbol)) {
                                    val oldCoin = initialCoinsMap[dto.symbol]!!
                                    val newPrice = dto.price.toDoubleOrNull() ?: 0.0

                                    val currentHistory = oldCoin.priceHistory.toMutableList()
                                    if (currentHistory.isNotEmpty()) {
                                        currentHistory[currentHistory.lastIndex] = newPrice
                                    } else {
                                        currentHistory.add(newPrice)
                                    }

                                    initialCoinsMap[dto.symbol] = oldCoin.copy(
                                        price = newPrice,
                                        high24h = dto.high.toDoubleOrNull() ?: 0.0,
                                        low24h = dto.low.toDoubleOrNull() ?: 0.0,
                                        changePercent = calculateChange(newPrice, dto.open.toDoubleOrNull()),
                                        priceHistory = currentHistory
                                    )
                                    hasChanges = true
                                }
                            }

                            if (hasChanges) {
                                emit(initialCoinsMap.values.toList())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun calculateChange(current: Double, open: Double?): Double {
        if (open == null || open == 0.0) return 0.0
        return ((current - open) / open) * 100
    }

    override suspend fun connect() {}
    override suspend fun disconnect() {}
}