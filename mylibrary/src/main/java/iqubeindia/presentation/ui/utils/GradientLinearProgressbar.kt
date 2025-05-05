package com.tvsm.iqubeindia.presentation.ui.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tvsm.connect.R

@Composable
fun GradientLinearProgressbar(
    isCharging : Boolean = false,
    progressPercentage: Float,
    modifier : Modifier = Modifier
        .fillMaxWidth()
        .height(5.dp),
    backgroundIndicatorColor: Color = Color.LightGray.copy(alpha = 0.3f),
    gradientColors: List<Color>,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    val animateNumber = animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    val tipImage: ImageBitmap = ImageBitmap.imageResource(R.drawable.ic_soc_charging_indicator)

    Canvas(
        modifier = modifier
    ) {

        // Background indicator
        drawLine(
            color = backgroundIndicatorColor,
            cap = StrokeCap.Round,
            strokeWidth = size.height,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = size.width, y = 0f)
        )

        // Convert the downloaded percentage into progress (width of foreground indicator)
        val progress = (animateNumber.value / 100) * size.width // size.width returns the width of the canvas

        // Foreground indicator
        drawLine(
            brush = Brush.linearGradient(
                colors = gradientColors
            ),
            cap = StrokeCap.Round,
            strokeWidth = size.height,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = progress, y = 0f)
        )

        // Calculate the image position
        val imageX = progress-25
        val imageY = -25f
        // Draw the image on the progress tip
        if (isCharging) {
            drawImage(image = tipImage, topLeft = Offset(x = imageX, y = imageY))
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun GLPbarPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            GradientLinearProgressbar(
                isCharging = true,
                progressPercentage = 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                gradientColors = listOf(
                    colorResource(id = R.color.socNormalGradientColor1),
                    colorResource(id = R.color.socNormalGradientColor2)
                )
            )
        }
    }
}