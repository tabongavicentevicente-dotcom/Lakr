package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary Premium Dark Color Scheme (Active)
private val LaKrDarkColorScheme = darkColorScheme(
    primary = RosePrimary,
    onPrimary = Color.White,
    primaryContainer = RoseTertiary,
    onPrimaryContainer = CocoaDark,
    secondary = RoseSecondary,
    onSecondary = Color.Black,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = CocoaDark,
    tertiary = GoldChampagne,
    onTertiary = Color.Black,
    background = RoseBackground,
    onBackground = CocoaDark,
    surface = RoseWhite,
    onSurface = CocoaDark,
    surfaceVariant = Color(0xFF1E1A29),
    onSurfaceVariant = CocoaDark,
    outline = RoseTertiary,
    outlineVariant = RoseWhite,
    error = Color(0xFFCE93D8),
    onError = Color(0xFF1F0038)
)

// Standardize Light Scheme to match Premium Dark as well, forcing Dark Mode throughout!
private val LaKrLightColorScheme = LaKrDarkColorScheme


@Composable
fun LaKrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LaKrDarkColorScheme else LaKrLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
