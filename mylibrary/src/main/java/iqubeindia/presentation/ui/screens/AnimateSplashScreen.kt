package com.tvsm.iqubeindia.presentation.ui.screens

import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.wear.compose.material.MaterialTheme
import com.tvsm.connect.R
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun AnimateSplashScreen() {
    val TAG = "AnimateplashScreen"

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val screenSizeType = getScreenSizeType(LocalContext.current.resources.displayMetrics, LocalDensity.current.density)
        if (screenSizeType == ScreenSizeType.SMALL_ROUND)
            AnimatedLogoToTop(endY = -160f)
        else
            AnimatedLogoToTop(endY = -190f)
    }
}

@Composable
fun AnimatedLogoToTop(
    startY: Float = 0f, // Adjust initial starting position
    endY: Float = -160f, // Adjust final position at the top
) {
    val animationDuration = 1000
    val animationOffset = remember { Animatable(startY) }

    val ic_logo = painterResource(id = R.drawable.ic_tvs_logo_splash)
    val currentSize = lerp(100.dp, 50.dp,
        animationOffset.value / (endY - startY))

    Image(modifier = Modifier
        .offset {
            IntOffset(0, animationOffset.value.roundToInt())
        }
        .size(size = currentSize),
        painter = painterResource(id = R.drawable.ic_tvs_logo_splash),
        contentDescription = "Logo"
    )

    LaunchedEffect(Unit) {
        animationOffset.animateTo(
            targetValue = endY,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }
}

enum class ScreenSizeType {
    SMALL_ROUND,
    LARGE_ROUND,
    OTHER
}

fun getScreenSizeType(displayMetrics: DisplayMetrics, density: Float): ScreenSizeType {
    val screenDiagonalSize = (displayMetrics.widthPixels / density).toDouble().pow(2) +
            (displayMetrics.heightPixels / density).toDouble().pow(2)
    val screenDiagonalSizeInches = Math.sqrt(screenDiagonalSize)
    Log.d("SplashScreen", "screenDiagonalSizeInches: $screenDiagonalSizeInches")

    return when {
        screenDiagonalSizeInches < 272 -> ScreenSizeType.SMALL_ROUND // Adjust this threshold as needed
        screenDiagonalSizeInches >= 273 && screenDiagonalSizeInches <= 322 -> ScreenSizeType.LARGE_ROUND
        else -> ScreenSizeType.OTHER
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    AnimateSplashScreen()
}

