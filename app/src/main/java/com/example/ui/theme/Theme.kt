package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UniCyanSoft,
    onPrimary = UniNavyDark,
    primaryContainer = UniNavyLight,
    onPrimaryContainer = Color.White,
    secondary = GoldLight,
    onSecondary = UniNavyDark,
    secondaryContainer = GoldDark,
    onSecondaryContainer = Color.White,
    tertiary = EmeraldSuccess,
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = CardSurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = UniNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = UniNavyLight,
    onPrimaryContainer = Color.White,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = UniNavyDark,
    tertiary = EmeraldDark,
    background = SurfaceLight,
    surface = SurfaceLight,
    surfaceVariant = CardSurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
