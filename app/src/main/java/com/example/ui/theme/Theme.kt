package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MaskokiColorScheme = darkColorScheme(
    primary = OrangePrimary,
    background = DarkBackground,
    surface = DarkSurface,
    error = WarningRed,
    onPrimary = WhiteText,
    onBackground = WhiteText,
    onSurface = WhiteText,
    onError = WhiteText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = MaskokiColorScheme, typography = Typography, content = content)
}
