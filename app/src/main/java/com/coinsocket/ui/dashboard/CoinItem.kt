package com.coinsocket.ui.dashboard

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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

@Composable
fun CoinItem(
    coin: Coin,
    modifier: Modifier = Modifier
) {
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant

    val priceColor = remember { Animatable(defaultColor) }

    var previousPrice by remember { mutableDoubleStateOf(coin.price) }

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
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = coin.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$ ${coin.price}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = priceColor.value
            )
        }
    }
}