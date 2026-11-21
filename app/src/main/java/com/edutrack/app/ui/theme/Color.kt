package com.edutrack.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// EduTrack Theme Color System
// Light Mode: Classic Burgundy + Antique Gold Heritage (Unchanged)
// Dark Mode: Clean, Calm, Aesthetic Nordic Pine / Espresso Teak Slate
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
// CLEAN, CALM & AESTHETIC DARK MODE
// Deep Obsidian Pine Slate with Sage & Soft Antique Amber accents
// Low eye strain, tranquil contrast, clean modern look
// ==========================================
val DarkBg = Color(0xFF0F1719)                // Deep calm slate black-teal night
val DarkSurface = Color(0xFF162225)           // Serene card container surface
val DarkCardBg = Color(0xFF1D2C30)            // Elevated calm slate card
val DarkBorderSubtle = Color(0xFF283D42)      // Clean subtle divider border
val DarkSurfaceElevated = Color(0xFF23363B)

// Dark Mode Accent Highlights (Soft, serene & peaceful)
val DarkPrimaryTeal = Color(0xFF6BB5BF)       // Soft radiant teal
val DarkSecondarySpruce = Color(0xFF8EC3C8)   // Calm spruce teal
val DarkAccentSage = Color(0xFFA8B8B8)        // Silvery tranquil sage
val DarkWarmAmber = Color(0xFFE0B083)         // Soft calm teak amber

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
val AccentBurgundyDarkContainer = Color(0xFF25373B)

val CoralRed = Color(0xFFB91C1C)
val CoralRedLight = Color(0xFFFEE2E2)

val AccentBlue = BurgundySecondary
val AccentBlueLight = Color(0xFFF6EEF1)
val AccentBlueDarkContainer = Color(0xFF1E3337)

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
val ChipInactiveBgDark = Color(0xFF203236)
val ChipInactiveTextDark = Color(0xFFA5BDBD)

// Typography - Light Mode
val TextDark = Color(0xFF260D17)              // Very deep vintage wine/black
val TextMuted = Color(0xFF6B555D)             // Warm muted burgundy slate
val TextLight = Color(0xFF968087)

// Typography - Dark Mode (Soft, clear, high legibility)
val TextLightHighContrast = Color(0xFFF1F6F6) // Pure soft mist
val TextLightMuted = Color(0xFFA8BCBE)        // Calm sage text
val TextLightSubtle = Color(0xFF728A8D)

// Progress Bar Colors
val ProgressTrack = Color(0xFFEADBCE)
val ProgressTrackDark = Color(0xFF24383D)
val ProgressPurple = AntiqueGold

// Heritage Borders & Divider lines
val BorderSubtle = Color(0xFFE5D7CA)
val BorderSoft = Color(0xFFF0E5D9)
