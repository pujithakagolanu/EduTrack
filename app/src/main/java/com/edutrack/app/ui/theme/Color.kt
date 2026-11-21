package com.edutrack.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// EduTrack Theme Color System
// Burgundy + Antique Gold (Classic Luxury & Academic Heritage)
// ==========================================

// User Specified Core Heritage Colors
val BurgundyPrimary = Color(0xFF481C2B)       // Primary
val BurgundySecondary = Color(0xFF702F40)     // Secondary
val AntiqueGold = Color(0xFFC2A16B)           // Accent
val HeritageLightBg = Color(0xFFFAF7F3)       // Background
val HeritageLightSurface = Color(0xFFF1E6DC)  // Surface

// Light Mode Surfaces & Cards
val LightBg = HeritageLightBg
val LightSurface = Color(0xFFFFFFFF)          // Crisp inner cards on surface
val LightCardBg = Color(0xFFFFFFFF)
val LightSurfaceElevated = HeritageLightSurface

// Dark Mode Surfaces (Deep Velvet Wine, Rich Night & Obsidian)
val DarkBg = Color(0xFF160E12)                // Deep velvety midnight wine
val DarkSurface = Color(0xFF23161C)           // Rich burgundy charcoal surface
val DarkCardBg = Color(0xFF2E1C25)            // Elevated burgundy card
val DarkBorderSubtle = Color(0xFF3F2733)      // Subtle warm dark border

// Backward compatibility aliases
val SmartBg = LightBg
val SmartSurface = LightSurface
val SmartCardBg = LightCardBg

// Brand Accents
val AccentGold = AntiqueGold
val AccentGoldLight = Color(0xFFF7F1E5)
val AccentGoldDark = Color(0xFFDFBF88)
val AccentGoldDarkContainer = Color(0xFF3D311F)

val AccentBurgundyLight = Color(0xFFF5EBF0)
val AccentBurgundyDarkContainer = Color(0xFF3B1723)

val CoralRed = Color(0xFFB91C1C)
val CoralRedLight = Color(0xFFFEE2E2)

val AccentBlue = BurgundySecondary
val AccentBlueLight = Color(0xFFF6EEF1)
val AccentBlueDarkContainer = Color(0xFF461E28)

val AccentMint = Color(0xFF2E6B56)
val AccentMintLight = Color(0xFFE8F3EE)
val AccentMintDarkContainer = Color(0xFF153329)

val AccentPurple = BurgundyPrimary
val AccentPurpleLight = AccentBurgundyLight
val AccentPurpleDarkContainer = AccentBurgundyDarkContainer

val AccentOrange = AntiqueGold
val AccentOrangeLight = AccentGoldLight

val ChipInactiveBg = Color(0xFFEDE5DC)
val ChipInactiveText = Color(0xFF6B585E)
val ChipInactiveBgDark = Color(0xFF332029)
val ChipInactiveTextDark = Color(0xFFBFAAA2)

// Typography - Light Mode
val TextDark = Color(0xFF260D17)              // Very deep vintage wine/black
val TextMuted = Color(0xFF6B555D)             // Warm muted burgundy slate
val TextLight = Color(0xFF968087)

// Typography - Dark Mode
val TextLightHighContrast = Color(0xFFFAF4F0) // Warm ivory cream
val TextLightMuted = Color(0xFFD4C2B8)        // Soft antique parchment

// Progress Bar Colors
val ProgressTrack = Color(0xFFEADBCE)
val ProgressTrackDark = Color(0xFF3D2631)
val ProgressPurple = AntiqueGold

// Heritage Borders & Divider lines
val BorderSubtle = Color(0xFFE5D7CA)
val BorderSoft = Color(0xFFF0E5D9)

