package com.tvsm.iqubeindia.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val DarkColorPalette = Colors(
    primary = colorPrimaryDark,
    primaryVariant = colorPrimary,
    secondary = colorAccent,
    onPrimary = Color.White
)

private val LightColorPalette = Colors(
    primary = colorPrimary,
    primaryVariant = colorPrimaryDark,
    secondary = colorAccent,
    onPrimary = Color.White
)

@Composable
fun TVSElectricTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}