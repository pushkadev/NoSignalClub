package com.smsgateway24.nosignalclub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DeepBlack,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonGreen,
    secondary = TextSecondary,
    onSecondary = DeepBlack,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningAmber,
    background = DeepBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = DangerRed,
    onError = DeepBlack,
)

@Composable
fun NoSignalClubTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}