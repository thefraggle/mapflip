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

/**
 * Refined map pin "flip" animation.
 * Two pins smoothly cross-fade with a subtle curved arrow arc.
 */
@Composable
fun MapFlipAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "flip")

    // Smooth ease-in-out with a longer pause at the end for breathing room
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                0f at 0 using EaseInOutCubic
                1f at 2200 using EaseInOutCubic
                1f at 5000 // long pause
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val pinGray = Color(0xFFB0BEC5)   // Subtle gray for "from" pin
    val pinAccent = MaterialTheme.colorScheme.primary
    val arrowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    Canvas(modifier = modifier.size(160.dp)) {
        val w = size.width
        val h = size.height
        val pinW = w * 0.16f
        val pinH = h * 0.30f

        // Left pin – slides slightly right and fades out
        val leftX = w * 0.32f + (w * 0.08f * progress)
        val leftAlpha = (1f - progress * 1.5f).coerceIn(0f, 0.8f)
        val leftScale = 1f - progress * 0.15f
        drawMapPin(Offset(leftX, h * 0.56f), pinW * leftScale, pinH * leftScale, pinGray, leftAlpha)

        // Right pin – slides slightly left and fades in
        val rightX = w * 0.68f - (w * 0.08f * (1f - progress))
        val rightAlpha = (progress * 1.5f).coerceIn(0f, 1f)
        val rightScale = 0.85f + progress * 0.15f
        drawMapPin(Offset(rightX, h * 0.56f), pinW * rightScale, pinH * rightScale, pinAccent, rightAlpha)

        // Curved arrow – draws progressively
        val arrowProgress = (progress * 1.4f).coerceIn(0f, 1f)
        if (arrowProgress > 0.05f) {
            val arrowPath = Path().apply {
                moveTo(w * 0.35f, h * 0.24f)
                cubicTo(
                    w * 0.43f, h * 0.10f,
                    w * 0.57f, h * 0.10f,
                    w * 0.65f, h * 0.24f
                )
            }

            // Create a partial path effect based on progress
            val pathMeasure = PathMeasure()
            pathMeasure.setPath(arrowPath, false)
            val partialPath = Path()
            pathMeasure.getSegment(0f, pathMeasure.length * arrowProgress, partialPath, true)

            drawPath(
                path = partialPath,
                color = arrowColor.copy(alpha = arrowProgress * 0.7f),
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // Arrow head – appears at the end
            if (arrowProgress > 0.7f) {
                val headAlpha = ((arrowProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)
                val headPath = Path().apply {
                    moveTo(w * 0.61f, h * 0.16f)
                    lineTo(w * 0.65f, h * 0.24f)
                    lineTo(w * 0.57f, h * 0.26f)
                }
                drawPath(
                    path = headPath,
                    color = arrowColor.copy(alpha = headAlpha * 0.7f),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

private fun DrawScope.drawMapPin(
    center: Offset, width: Float, height: Float, color: Color, alpha: Float
) {
    if (alpha <= 0f) return

    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = alpha * 0.06f),
        radius = width * 0.45f,
        center = Offset(center.x + 1.dp.toPx(), center.y + height / 2 + 2.dp.toPx())
    )

    // Pin body (teardrop)
    val path = Path().apply {
        addOval(Rect(
            left = center.x - width / 2,
            top = center.y - height / 2,
            right = center.x + width / 2,
            bottom = center.y - height / 2 + width
        ))
        moveTo(center.x - width * 0.28f, center.y - height / 2 + width * 0.72f)
        lineTo(center.x, center.y + height / 2)
        lineTo(center.x + width * 0.28f, center.y - height / 2 + width * 0.72f)
        close()
    }
    drawPath(path, color.copy(alpha = alpha))

    // Inner circle (white dot)
    drawCircle(
        Color.White.copy(alpha = alpha * 0.92f),
        radius = width * 0.17f,
        center = Offset(center.x, center.y - height / 2 + width / 2)
    )
}
