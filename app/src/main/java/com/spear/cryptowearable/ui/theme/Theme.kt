package com.spear.cryptowearable.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.*

// Dark colors optimized for AMOLED
private val Green = Color(0xFF00C853)
private val Red = Color(0xFFD50000)
private val DarkBackground = Color(0xFF000000)
private val DarkSurface = Color(0xFF1A1A1A)
private val LightGray = Color(0xFFB0B0B0)

private val DarkColorPalette = Colors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = DarkBackground,
    surface = DarkSurface,
    error = Red,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = LightGray,
    onError = Color.Black
)

@Composable
fun CryptoWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography(),
        content = content
    )
}

// Utility colors
object CryptoColors {
    val Positive = Green
    val Negative = Red
}
