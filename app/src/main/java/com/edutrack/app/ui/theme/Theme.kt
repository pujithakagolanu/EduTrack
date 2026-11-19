package com.edutrack.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    onPrimary = LightSurface,
    primaryContainer = AccentPurpleLight,
    onPrimaryContainer = TextDark,
    secondary = AccentBlue,
    onSecondary = LightSurface,
    secondaryContainer = AccentBlueLight,
    onSecondaryContainer = TextDark,
    tertiary = AccentMint,
    onTertiary = LightSurface,
    tertiaryContainer = AccentMintLight,
    background = LightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurface,
    onSurfaceVariant = TextMuted,
    outline = BorderSubtle,
    outlineVariant = BorderSoft,
    error = CoralRed,
    onError = LightSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = TextDark,
    primaryContainer = AccentPurpleDarkContainer,
    onPrimaryContainer = TextLightHighContrast,
    secondary = AccentBlue,
    onSecondary = TextDark,
    secondaryContainer = AccentBlueDarkContainer,
    onSecondaryContainer = TextLightHighContrast,
    tertiary = AccentMint,
    onTertiary = TextDark,
    tertiaryContainer = AccentMintDarkContainer,
    background = DarkBg,
    onBackground = TextLightHighContrast,
    surface = DarkSurface,
    onSurface = TextLightHighContrast,
    surfaceVariant = DarkCardBg,
    onSurfaceVariant = TextLightMuted,
    outline = DarkBorderSubtle,
    outlineVariant = DarkBorderSubtle,
    error = CoralRed,
    onError = LightSurface
)

@Composable
fun EduTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                val statusBarColor = if (darkTheme) DarkBg else LightBg
                val navBarColor = if (darkTheme) DarkSurface else LightSurface
                it.statusBarColor = statusBarColor.toArgb()
                it.navigationBarColor = navBarColor.toArgb()
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
