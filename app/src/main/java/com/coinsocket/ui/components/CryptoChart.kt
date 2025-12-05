package com.coinsocket.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CryptoChart(
    dataPoints: List<Double>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {

        if (dataPoints.size < 2) return@Canvas

        val width = size.width
        val height = size.height

        val maxVal = dataPoints.maxOrNull() ?: 0.0
        val minVal = dataPoints.minOrNull() ?: 0.0
        val range = maxVal - minVal

        val plotPoints = if (range == 0.0) {
            dataPoints.map { height / 2 }
        } else {
            dataPoints.map { price ->
                val ratio = (price - minVal) / range
                height - (ratio * height).toFloat()
            }
        }

        val spacing = width / (dataPoints.size - 1)

        val strokePath = Path().apply {
            moveTo(0f, plotPoints.first())

            for (i in 0 until plotPoints.size - 1) {
                val p1 = plotPoints[i]
                val p2 = plotPoints[i + 1]

                val x1 = i * spacing
                val y1 = p1
                val x2 = (i + 1) * spacing
                val y2 = p2

                val cx = (x1 + x2) / 2f
                quadraticTo(x1, y1, cx, (y1 + y2) / 2f)
            }
            lineTo(width, plotPoints.last())
        }

        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    Color.Transparent
                )
            )
        )

        drawPath(
            path = strokePath,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}