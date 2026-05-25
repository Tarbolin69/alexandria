package com.libreria.alexandria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val YellowDarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = YellowOnPrimary,
    primaryContainer = YellowPrimaryContainer,
    onPrimaryContainer = YellowOnPrimaryContainer,
    secondary = YellowSecondary,
    onSecondary = YellowOnSecondary,
    secondaryContainer = YellowSecondaryContainer,
    onSecondaryContainer = YellowOnSecondaryContainer,
    tertiary = YellowTertiary,
    onTertiary = YellowOnTertiary,
    background = YellowBackground,
    onBackground = YellowOnBackground,
    surface = YellowSurface,
    onSurface = YellowOnSurface,
    surfaceVariant = YellowSurfaceVariant,
    onSurfaceVariant = YellowOnSurfaceVariant,
    outline = YellowOutline,
    error = ErrorRed,
    onError = OnError
)

@Composable
fun AlexandriaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = YellowDarkColorScheme,
        typography = Typography,
        content = content
    )
}
