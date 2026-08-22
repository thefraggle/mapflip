package de.goork.mapflip.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Blue800,
    onPrimaryContainer = Blue90,
    secondary = Blue90,
    onSecondary = Blue10,
    surface = DarkSurface,
    onSurface = Color(0xFFE3E3E3),
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = Color(0xFFC4C7C5),
    surfaceContainerLowest = Color(0xFF0E0E0E),
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = Color(0xFF333538),
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = Red500,
    onError = Color.White,
    tertiary = Orange500,
    tertiaryContainer = Color(0xFF3E2D04)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue800,
    secondary = Blue800,
    onSecondary = Color.White,
    surface = LightSurface,
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = LightSurfaceContainerHigh,
    onSurfaceVariant = Color(0xFF44474F),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = Color(0xFFDFE2EB),
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = Red500,
    onError = Color.White,
    tertiary = Orange500,
    tertiaryContainer = Color(0xFFFFF0D4)
)

@Composable
fun MapFlipTheme(
    themePref: String = "system",
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themePref) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MapFlipTypography,
        content = content
    )
}
