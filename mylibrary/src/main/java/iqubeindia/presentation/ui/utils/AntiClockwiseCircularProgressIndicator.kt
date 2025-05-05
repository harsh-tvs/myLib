package com.tvsm.iqubeindia.presentation.ui.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min

@Composable
fun AntiClockwiseCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Blue,
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress = animateFloatAsState(targetValue = progress)
    //val animatedProgress = animateFloatAsState(targetValue = 1 - progress)
    val startAngle: Float = 270f
    val endAngle: Float = startAngle
    val backgroundSweep = 360f - ((startAngle - endAngle) % 360 + 360) % 360
    val progressSweep = -backgroundSweep * progress.coerceIn(0f..1f)

    Canvas(modifier = modifier) {
        val canvasSize = Size(size.width, size.height)
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Draw progress arc
        drawArc(
            color = color,
            startAngle = 270f,
            sweepAngle = progressSweep,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}
