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
    primary = DarkPrimaryTeal,                      // Clean luminous teal for clean legibility
    onPrimary = Color(0xFF091B1E),
    primaryContainer = Color(0xFF16343A),
    onPrimaryContainer = Color(0xFFD7EEF2),
    secondary = DarkWarmAmber,                      // Soft serene teak amber
    onSecondary = Color(0xFF28190B),
    secondaryContainer = Color(0xFF382618),
    onSecondaryContainer = Color(0xFFF5E4D4),
    tertiary = DarkAccentSage,                      // Silvery calm sage
    onTertiary = Color(0xFF102123),
    tertiaryContainer = Color(0xFF1E3539),
    onTertiaryContainer = Color(0xFFD6E3E3),
    background = DarkBg,                            // Deep calm obsidian slate #0F1719
    onBackground = TextLightHighContrast,
    surface = DarkSurface,                          // #162225 calm surface
    onSurface = TextLightHighContrast,
    surfaceVariant = DarkCardBg,                    // #1D2C30 elevated calm card
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
