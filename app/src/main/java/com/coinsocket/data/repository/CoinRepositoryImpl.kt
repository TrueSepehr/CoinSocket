package com.coinsocket.data.repository

import com.coinsocket.data.remote.dto.CoinDto
import com.coinsocket.data.remote.dto.toCoin
import com.coinsocket.domain.model.Coin
import com.coinsocket.domain.repository.CoinRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
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

    override fun observeCoinPrices(): Flow<List<Coin>> = flow {
        val coinCache = popularCoins.associateWith { symbol ->
            Coin(
                symbol = symbol,
                price = 0.0,
                high24h = 0.0,
                low24h = 0.0,
                openPrice = 0.0
            )
        }.toMutableMap()

        try {
            client.webSocket(urlString = "wss://stream.binance.com:9443/ws/!miniTicker@arr") {
                emit(coinCache.values.toList())
                while (isActive) {
                    val frame = incoming.receive()
                    if (frame is Frame.Text) {
                        try {
                            val jsonString = frame.readText()
                            val dtos = jsonParser.decodeFromString<List<CoinDto>>(jsonString)
                            var hasChanges = false
                            dtos.forEach { dto ->
                                if (popularCoins.contains(dto.symbol)) {
                                    coinCache[dto.symbol] = dto.toCoin()
                                    hasChanges = true
                                }
                            }
                            if (hasChanges) {
                                emit(coinCache.values.toList())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val errorMessage = when(e) {
                is HttpRequestTimeoutException -> "Connection Timed Out (5s limit)"
                is ConnectException -> "Failed to connect. Check internet."
                is UnknownHostException -> "Server not found."
                is SocketException -> "Connection lost unexpectedly."
                else -> e.message ?: "Unknown Error occurred"
            }
            throw Exception(errorMessage)
        }
    }

    override suspend fun connect() {}
    override suspend fun disconnect() {}
}