package com.coinsocket.ui.dashboard

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coinsocket.domain.model.Coin
import com.coinsocket.ui.components.CryptoChart
import java.util.Locale

@Composable
fun CoinItem(
    coin: Coin,
    modifier: Modifier = Modifier
) {
    val defaultTextColor = MaterialTheme.colorScheme.onSurface
    val upColor = Color(0xFF00C853)
    val downColor = Color(0xFFD50000)
    val priceAnimatable = remember { Animatable(defaultTextColor) }
    val previousPriceRef = remember { object { var value = coin.price } }
    val coinColor = getCoinSpecificColor(coin.symbol)

    LaunchedEffect(coin.price) {
        val oldPrice = previousPriceRef.value

        if (coin.price > oldPrice) {
            priceAnimatable.snapTo(upColor)
        } else if (coin.price < oldPrice) {
            priceAnimatable.snapTo(downColor)
        }

        if (coin.price != oldPrice) {
            priceAnimatable.animateTo(
                targetValue = defaultTextColor,
                animationSpec = tween(durationMillis = 600)
            )
            previousPriceRef.value = coin.price
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(coinColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = coin.symbol.take(1),
                        fontWeight = FontWeight.Bold,
                        color = coinColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = coin.symbol.removeSuffix("USDT"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            CryptoChart(
                dataPoints = coin.priceHistory,
                color = coinColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$${formatPrice(coin.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = priceAnimatable.value,
                    maxLines = 1
                )
                Text(
                    text = "${if (coin.changePercent >= 0) "+" else ""}${String.format(Locale.US, "%.2f", coin.changePercent)}%",
                    color = if (coin.changePercent >= 0) upColor else downColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun formatPrice(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

fun getCoinSpecificColor(symbol: String): Color {
    return when {
        symbol.contains("BTC") -> Color(0xFFF7931A) // Bitcoin Orange
        symbol.contains("ETH") -> Color(0xFF627EEA) // Ethereum Blue/Purple
        symbol.contains("LTC") -> Color(0xFF345D9D) // Litecoin Blue
        symbol.contains("XRP") -> Color(0xFF23292F) // Ripple Black/Dark
        symbol.contains("BNB") -> Color(0xFFF3BA2F) // Binance Yellow
        symbol.contains("SOL") -> Color(0xFF14F195) // Solana Green
        symbol.contains("DOGE") -> Color(0xFFC2A633) // Doge Yellow
        symbol.contains("DOT") -> Color(0xFFE6007A) // Polkadot Pink
        else -> Color(0xFF757575) // Default Grey
    }
}