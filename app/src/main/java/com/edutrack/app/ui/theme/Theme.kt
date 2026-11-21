package com.edutrack.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AccentBurgundyLight,
    onPrimaryContainer = BurgundyPrimary,
    secondary = BurgundySecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF7ECEF),
    onSecondaryContainer = BurgundySecondary,
    tertiary = AntiqueGold,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = AccentGoldLight,
    onTertiaryContainer = Color(0xFF4A3816),
    background = HeritageLightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = HeritageLightSurface,
    onSurfaceVariant = TextMuted,
    outline = BorderSubtle,
    outlineVariant = BorderSoft,
    error = CoralRed,
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8B2C4),                    // Soft rose burgundy for dark mode primary
    onPrimary = BurgundyPrimary,
    primaryContainer = BurgundyPrimary,
    onPrimaryContainer = Color(0xFFFDE8EF),
    secondary = AccentGoldDark,                     // Warm antique gold
    onSecondary = Color(0xFF2E1C0A),
    secondaryContainer = AccentGoldDarkContainer,
    onSecondaryContainer = Color(0xFFFBF2E3),
    tertiary = Color(0xFF5AB697),
    onTertiary = Color(0xFF0F261E),
    tertiaryContainer = AccentMintDarkContainer,
    background = DarkBg,
    onBackground = TextLightHighContrast,
    surface = DarkSurface,
    onSurface = TextLightHighContrast,
    surfaceVariant = DarkCardBg,
    onSurfaceVariant = TextLightMuted,
    outline = DarkBorderSubtle,
    outlineVariant = DarkBorderSubtle,
    error = Color(0xFFF87171),
    onError = Color(0xFFFFFFFF)
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
