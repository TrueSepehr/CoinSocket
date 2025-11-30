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

    val isPositive = coin.changePercent >= 0
    val trendColor = if (isPositive) upColor else downColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(trendColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = coin.symbol.removeSuffix("USDT"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${formatPrice(coin.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = priceAnimatable.value
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", coin.changePercent)}%",
                        color = trendColor,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PriceRangeBar(
                current = coin.price,
                low = coin.low24h,
                high = coin.high24h,
                color = trendColor
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "L: ${formatPrice(coin.low24h)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "H: ${formatPrice(coin.high24h)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatPrice(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

@Composable
fun PriceRangeBar(
    current: Double,
    low: Double,
    high: Double,
    color: Color
) {
    val range = high - low
    val progress = if (range == 0.0) 0.5 else (current - low) / range

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.toFloat().coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(color, RoundedCornerShape(4.dp))
        )
    }
}