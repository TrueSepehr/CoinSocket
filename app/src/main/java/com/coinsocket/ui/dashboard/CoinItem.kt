package com.coinsocket.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coinsocket.domain.model.Coin

@SuppressLint("DefaultLocale")
@Composable
fun CoinItem(
    coin: Coin,
    modifier: Modifier = Modifier
) {
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant

    val priceColor = remember { Animatable(defaultColor) }

    var previousPrice by remember { mutableDoubleStateOf(coin.price) }
    val isPositive = coin.changePercent >= 0
    val trendColor = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)

    LaunchedEffect(coin.price) {
        if (coin.price > previousPrice) {
            priceColor.snapTo(Color(0xFF00C853))
        } else if (coin.price < previousPrice) {
            priceColor.snapTo(Color(0xFFD50000))
        }
        priceColor.animateTo(
            targetValue = defaultColor,
            animationSpec = tween(durationMillis = 500)
        )

        previousPrice = coin.price
    }

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
                        text = "$${String.format("%.2f", coin.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", coin.changePercent)}%",
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
                    text = "L: ${coin.low24h}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "H: ${coin.high24h}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
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