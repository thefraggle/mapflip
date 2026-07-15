package de.goork.mapflip.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp

@Composable
fun MapFlipAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "flip")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                0f at 0 using FastOutSlowInEasing
                1f at 2000
                1f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val appleGray = Color(0xFFA2AAAD)
    val googleRed = Color(0xFFEA4335)
    val arrowColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.size(180.dp)) {
        val w = size.width
        val h = size.height
        val pinW = w * 0.2f
        val pinH = h * 0.33f

        // Left pin (Apple-style) fades out
        val leftX = w * 0.28f + (w * 0.12f * progress)
        val leftAlpha = (1f - progress * 1.3f).coerceIn(0f, 1f)
        drawMapPin(Offset(leftX, h * 0.55f), pinW, pinH, appleGray, leftAlpha)

        // Right pin (Google-style) fades in
        val rightX = w * 0.72f - (w * 0.12f * (1f - progress))
        val rightAlpha = (progress * 1.3f).coerceIn(0f, 1f)
        drawMapPin(Offset(rightX, h * 0.55f), pinW, pinH, googleRed, rightAlpha)

        // Curved arrow
        val arrowAlpha = if (progress < 0.7f) progress / 0.7f else 1f
        val arrowPath = Path().apply {
            moveTo(w * 0.32f, h * 0.22f)
            cubicTo(w * 0.45f, h * 0.06f, w * 0.55f, h * 0.06f, w * 0.68f, h * 0.22f)
        }
        drawPath(
            path = arrowPath,
            color = arrowColor.copy(alpha = arrowAlpha),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Arrow head
        if (arrowAlpha > 0.3f) {
            val headPath = Path().apply {
                moveTo(w * 0.63f, h * 0.13f)
                lineTo(w * 0.68f, h * 0.22f)
                lineTo(w * 0.58f, h * 0.24f)
            }
            drawPath(
                path = headPath,
                color = arrowColor.copy(alpha = arrowAlpha),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

private fun DrawScope.drawMapPin(
    center: Offset, width: Float, height: Float, color: Color, alpha: Float
) {
    val path = Path().apply {
        addOval(Rect(
            left = center.x - width / 2, top = center.y - height / 2,
            right = center.x + width / 2, bottom = center.y - height / 2 + width
        ))
        moveTo(center.x - width * 0.3f, center.y - height / 2 + width * 0.7f)
        lineTo(center.x, center.y + height / 2)
        lineTo(center.x + width * 0.3f, center.y - height / 2 + width * 0.7f)
        close()
    }
    drawPath(path, color.copy(alpha = alpha))
    drawCircle(
        Color.White.copy(alpha = alpha * 0.9f),
        radius = width * 0.18f,
        center = Offset(center.x, center.y - height / 2 + width / 2)
    )
}
