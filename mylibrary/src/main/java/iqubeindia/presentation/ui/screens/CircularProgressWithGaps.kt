package com.tvsm.iqubeindia.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.utils.WatchAppConfig
import kotlin.math.cos
import kotlin.math.sin

/**
 * A composable function that displays a semi-circular progress bar with individual rounded bars.
 * The progress is indicated by the number of bars filled with the `progressColor`.
 *
 * @param progress The current progress level (0 to 6), determining the number of filled bars.
 * @param modifier Modifier to apply to the layout of the progress bar.
 */
@Composable
fun SemiCircularBarsProgressBar(
    progress: Int,
    totalArcBars: Int = 6, // Make total number of bars configurable with a default
    criticalFuelLevel: Int = 1, // Make critical fuel level configurable with a default
    modifier: Modifier = Modifier
) {
    val alertColorForFuelLevel = colorResource(id = R.color.alertColorForFuelLevel)
    val progressColorForFuelLevel= colorResource(id = R.color.progressColorForFuelLevel)
    val progressBarBackgroundColor = colorResource(id = R.color.progressBarBackgroundColor)
    val progressColor by remember(progress) {

        mutableStateOf(if (progress == criticalFuelLevel) alertColorForFuelLevel else progressColorForFuelLevel)
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val size = constraints.minWidth.toFloat() // Assuming width and height are equal or we want to base on width
        val density = LocalDensity.current
        val centerOffsetYPx = with(density) { 5.dp.toPx() }
        val center = Offset(size / 2f, (size / 2f) + centerOffsetYPx) // Shift center downwards

        ArcBars(  // Call ArcBars for the background
            totalBars = totalArcBars,
            filledBars = totalArcBars,
            color =progressBarBackgroundColor,
            center = center,
            size = size,
        )
        ArcBars(  // Call ArcBars for the foreground (progress)
            totalBars = totalArcBars,
            filledBars = progress,
            color = progressColor,
            center = center,
            size = size,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp)
        ) {
            Spacer(modifier = Modifier.weight(2f))
            Image(
                painter = painterResource(id = R.drawable.group_54056), // Replace with your actual image resource ID
                contentDescription = "Fuel Logo",
                modifier = Modifier.size(28.dp) // Adjust size as needed
            )
            Text(
                text = "Fuel",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.roboto_medium)),
                    fontSize = 14.sp,
                    color = Color.Gray // Or your primary color
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * A composable function that draws a series of rounded arc bars.
 *
 * @param totalBars The total number of bars to draw.
 * @param filledBars The number of bars to draw as filled with the specified color.
 * @param color The color to use for the filled bars.
 * @param center The center point of the semi-circular arc.
 * @param size The overall size (width and height) used to calculate the arc dimensions.
 */
@Composable
private fun ArcBars(
    totalBars: Int,
    filledBars: Int,
    color: Color,
    center: Offset,
    size: Float,
) {
    val cornerRadiusDp = 6.dp // Define cornerRadius as Dp

    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { cornerRadiusDp.toPx() }

    Canvas(modifier = Modifier.size(size.dp)) { // Canvas within ArcBars
        drawArcBars(
            totalBars = totalBars,
            filledBars = filledBars,
            color = color,
            center = center,
            size = size,
            cornerRadiusPx = cornerRadiusPx
        )
    }
}

/**
 * A [DrawScope] extension function that draws the individual rounded arc bars on the canvas.
 *
 * @param totalBars The total number of bars to draw.
 * @param filledBars The number of bars to draw as filled.
 * @param color The color to use for the filled bars.
 * @param center The center point of the semi-circular arc.
 * @param size The overall size used to calculate dimensions.
 * @param cornerRadiusPx The corner radius of the rounded bars in pixels.
 */
private fun DrawScope.drawArcBars(
    totalBars: Int,
    filledBars: Int,
    color: Color,
    center: Offset,
    size: Float,
    cornerRadiusPx: Float
) {
    val startAngle = 150f
    val endAngle = -90f
    val totalAngle = kotlin.math.abs(endAngle - startAngle)

    val gapAngle = 6f // Space between bars
    val barAngle = (totalAngle - ((totalBars - 1) * gapAngle)) / totalBars

    val innerRadius = size * 0.4f
    val outerRadius = size * 0.5f

    for (index in 0 until totalBars) {
        val start = startAngle + index * (barAngle + gapAngle)
        val end = start + barAngle

        val path = roundedBarPath(
            center = center,
            innerRadius = innerRadius,
            outerRadius = outerRadius,
            startAngle = start,
            endAngle = end,
            cornerRadius = cornerRadiusPx
        )

        if (index < filledBars) {
            drawPath(
                path = path,
                color = color
            )
        }
    }
}

/**
 * A regular function that calculates and returns the [Path] for a single rounded arc bar.
 *
 * @param center The center point of the arc.
 * @param innerRadius The inner radius of the arc bar.
 * @param outerRadius The outer radius of the arc bar.
 * @param startAngle The starting angle of the arc in degrees.
 * @param endAngle The ending angle of the arc in degrees.
 * @param cornerRadius The corner radius of the rounded ends in pixels.
 * @return The [Path] representing the rounded arc bar.
 */
private fun roundedBarPath(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    startAngle: Float,
    endAngle: Float,
    cornerRadius: Float
): Path {
    return Path().apply {
        val startInner = Offset(
            x = center.x + cos(Math.toRadians(startAngle.toDouble())).toFloat() * innerRadius,
            y = center.y + sin(Math.toRadians(startAngle.toDouble())).toFloat() * innerRadius
        )
        val endInner = Offset(
            x = center.x + cos(Math.toRadians(endAngle.toDouble())).toFloat() * innerRadius,
            y = center.y + sin(Math.toRadians(endAngle.toDouble())).toFloat() * innerRadius
        )
        val startOuter = Offset(
            x = center.x + cos(Math.toRadians(startAngle.toDouble())).toFloat() * outerRadius,
            y = center.y + sin(Math.toRadians(startAngle.toDouble())).toFloat() * outerRadius
        )
        val endOuter = Offset(
            x = center.x + cos(Math.toRadians(endAngle.toDouble())).toFloat() * outerRadius,
            y = center.y + sin(Math.toRadians(endAngle.toDouble())).toFloat() * outerRadius
        )

        moveTo(startOuter.x, startOuter.y)

        // Create rounded path from start to end (outer arc)
        arcTo(
            rect = Rect(
                center = center,
                radius = outerRadius
            ),
            startAngleDegrees = startAngle,
            sweepAngleDegrees = endAngle - startAngle,
            forceMoveTo = false
        )

        lineTo(endInner.x, endInner.y)

        // Add inner part with the rounded corner (inner arc)
        arcTo(
            rect = Rect(
                center = center,
                radius = innerRadius
            ),
            startAngleDegrees = endAngle,
            sweepAngleDegrees = startAngle - endAngle,
            forceMoveTo = false // Same here
        )

        lineTo(startInner.x, startInner.y)

        close()
    }
}

// Preview function
@Preview(showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun SemiCircularBarsProgressBarPreview() {
    var progress by remember { mutableStateOf(2) }
    SemiCircularBarsProgressBar(progress = progress)
}