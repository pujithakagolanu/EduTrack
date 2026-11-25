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

// Option 1: Warm Charcoal & Cashmere Amber
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryCashmere,                  // Soft cashmere cream #E2C4A2
    onPrimary = Color(0xFF1F1A14),
    primaryContainer = Color(0xFF2C241C),
    onPrimaryContainer = Color(0xFFF2DFCE),
    secondary = DarkSecondaryAmber,                 // Muted teak amber #C49A74
    onSecondary = Color(0xFF241910),
    secondaryContainer = Color(0xFF2E2218),
    onSecondaryContainer = Color(0xFFEDE0D4),
    tertiary = DarkAccentWarmTaupe,                 // Gentle tranquil warm taupe
    onTertiary = Color(0xFF191816),
    tertiaryContainer = Color(0xFF2B2825),
    onTertiaryContainer = Color(0xFFE2DDD8),
    background = DarkBg,                            // Warm charcoal slate #141416
    onBackground = TextLightHighContrast,           // #EDEDF0
    surface = DarkSurface,                          // #1E1F24 warm graphite surface
    onSurface = TextLightHighContrast,
    surfaceVariant = DarkCardBg,                    // #26282E elevated card
    onSurfaceVariant = TextLightMuted,              // #9A9DA8
    outline = DarkBorderSubtle,                     // #2D3039
    outlineVariant = DarkBorderSubtle,
    error = Color(0xFFE57373),                      // Muted soft coral red (no eye sting)
    onError = Color(0xFF2A1010)
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
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
