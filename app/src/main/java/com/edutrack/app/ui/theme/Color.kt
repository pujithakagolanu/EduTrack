package com.edutrack.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// EduTrack Theme Color System
// Light Mode: Classic Burgundy + Antique Gold Heritage (Unchanged)
// Dark Mode: Option 1 - Warm Charcoal & Cashmere Amber (Calm, Cozy, Low Eye Strain)
// ==========================================

// User Specified Core Heritage Colors (LIGHT MODE PRESERVED)
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

// ==========================================
// OPTION 1: WARM CHARCOAL & CASHMERE AMBER (DARK MODE)
// Deep warm charcoal slate with cozy cashmere & soft amber highlights
// Gentle on the eyes, zero glare, natural contrast
// ==========================================
val DarkBg = Color(0xFF141416)                // Deep warm charcoal night
val DarkSurface = Color(0xFF1E1F24)           // Soothing warm graphite surface card
val DarkCardBg = Color(0xFF26282E)            // Elevated warm slate card
val DarkBorderSubtle = Color(0xFF2D3039)      // Clean soft border divider
val DarkSurfaceElevated = Color(0xFF2C2F36)

// Dark Mode Accent Highlights (Warm, calm & peaceful)
val DarkPrimaryCashmere = Color(0xFFE2C4A2)   // Soft cashmere cream primary
val DarkSecondaryAmber = Color(0xFFC49A74)    // Muted teak amber secondary
val DarkAccentWarmTaupe = Color(0xFFA69E96)   // Gentle tranquil taupe tertiary
val DarkContainerTint = Color(0xFF2A2622)     // Muted warm container tint

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
val AccentBurgundyDarkContainer = Color(0xFF2E2428)

val CoralRed = Color(0xFFB91C1C)
val CoralRedLight = Color(0xFFFEE2E2)

val AccentBlue = BurgundySecondary
val AccentBlueLight = Color(0xFFF6EEF1)
val AccentBlueDarkContainer = Color(0xFF292528)

val AccentMint = Color(0xFF2E6B56)
val AccentMintLight = Color(0xFFE8F3EE)
val AccentMintDarkContainer = Color(0xFF1D2924)

val AccentPurple = BurgundyPrimary
val AccentPurpleLight = AccentBurgundyLight
val AccentPurpleDarkContainer = AccentBurgundyDarkContainer

val AccentOrange = AntiqueGold
val AccentOrangeLight = AccentGoldLight

val ChipInactiveBg = Color(0xFFEDE5DC)
val ChipInactiveText = Color(0xFF6B585E)
val ChipInactiveBgDark = Color(0xFF282A31)
val ChipInactiveTextDark = Color(0xFF9E9FA8)

// Typography - Light Mode
val TextDark = Color(0xFF260D17)              // Very deep vintage wine/black
val TextMuted = Color(0xFF6B555D)             // Warm muted burgundy slate
val TextLight = Color(0xFF968087)

// Typography - Dark Mode (Warm, soft, high legibility without glare)
val TextLightHighContrast = Color(0xFFEDEDF0) // Soft mist off-white
val TextLightMuted = Color(0xFF9A9DA8)        // Calm warm slate text
val TextLightSubtle = Color(0xFF70737E)

// Progress Bar Colors
val ProgressTrack = Color(0xFFEADBCE)
val ProgressTrackDark = Color(0xFF2A2C34)
val ProgressPurple = AntiqueGold

// Heritage Borders & Divider lines
val BorderSubtle = Color(0xFFE5D7CA)
val BorderSoft = Color(0xFFF0E5D9)
