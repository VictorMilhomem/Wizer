package com.github.wizerapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = WizerBlue,
    onPrimary = WizerSurfaceLight,
    primaryContainer = WizerLightBlue,
    onPrimaryContainer = WizerDarkBlue,
    secondary = WizerOrange,
    onSecondary = WizerSurfaceLight,
    tertiary = WizerGreen,
    onTertiary = WizerSurfaceLight,
    background = WizerBackgroundLight,
    onBackground = WizerDarkGray,
    surface = WizerSurfaceLight,
    onSurface = WizerDarkGray,
    error = WizerRed,
    onError = WizerSurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = WizerLightBlue,
    onPrimary = WizerBackgroundDark,
    primaryContainer = WizerBlue,
    onPrimaryContainer = WizerLightBlue,
    secondary = WizerOrange,
    onSecondary = WizerBackgroundDark,
    tertiary = WizerGreen,
    onTertiary = WizerBackgroundDark,
    background = WizerBackgroundDark,
    onBackground = WizerLightGray,
    surface = WizerSurfaceDark,
    onSurface = WizerLightGray,
    error = WizerRed,
    onError = WizerBackgroundDark
)

@Composable
fun WizerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Alterado para false por padrão para manter nossa paleta
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

    // Opcional: Aplicar a cor primária na status bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}