package com.example.groww.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GrowwGreen = Color(0xFF00D09C)
val GrowwBlack = Color(0xFF1B1B1B)
val GrowwGray = Color(0xFF7F7F7F)

private val DarkColorScheme = darkColorScheme(
    primary = GrowwGreen,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = GrowwBlack,
    background = GrowwBlack,
    surface = GrowwBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GrowwGreen,
    onPrimary = Color.White,
    secondary = GrowwBlack,
    onSecondary = Color.White,
    background = Color(0xFFF8F8F8),
    surface = Color.White,
    onBackground = GrowwBlack,
    onSurface = GrowwBlack
)

@Composable
fun GrowwTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
