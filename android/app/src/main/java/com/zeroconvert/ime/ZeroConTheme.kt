package com.zeroconvert.ime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ZeroConPalette(
    val isDark: Boolean,
    val canvas: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val keyNormal: Color,
    val keyModifier: Color,
    val primaryAccent: Color,
    val primaryAccentSoft: Color,
    val onAccent: Color,
    val readyEmerald: Color,
    val warningAmber: Color,
    val danger: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val borderSubtle: Color
)

/**
 * A selectable color theme. The ids and labels mirror
 * `windows/crates/shared/themes.json` so both platforms offer the same choices.
 */
data class ZeroConTheme(
    val id: String,
    val label: String,
    val palette: ZeroConPalette
) {
    val isDark: Boolean get() = palette.isDark
}

/**
 * Mirrors the Windows settings "dark_blue" palette so the Android settings
 * screen and keyboard share the same visual language.
 */
val ZeroConDarkBluePalette = ZeroConPalette(
    isDark = true,
    canvas = Color(0xFF101722),
    surface = Color(0xFF182334),
    surfaceElevated = Color(0xFF24344B),
    keyNormal = Color(0xFF24344B),
    keyModifier = Color(0xFF1B2A3F),
    primaryAccent = Color(0xFF74B5FF),
    primaryAccentSoft = Color(0xFF9CCBFF),
    onAccent = Color(0xFF10213A),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF87171),
    textPrimary = Color(0xFFEDF3FC),
    textSecondary = Color(0xFFA5B6CC),
    textMuted = Color(0xFF91A5BD),
    borderSubtle = Color(0xFF344760)
)

val ZeroConGraphitePalette = ZeroConPalette(
    isDark = true,
    // Toned down from the Windows values: the near-white accent and text glared on a phone.
    canvas = Color(0xFF1E1F22),
    surface = Color(0xFF26282C),
    surfaceElevated = Color(0xFF323439),
    keyNormal = Color(0xFF323439),
    keyModifier = Color(0xFF292B2F),
    primaryAccent = Color(0xFF8A919D),
    primaryAccentSoft = Color(0xFFA3A9B3),
    onAccent = Color(0xFF16181B),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF87171),
    textPrimary = Color(0xFFD9DBDF),
    textSecondary = Color(0xFFA3A7AE),
    textMuted = Color(0xFF8E9299),
    borderSubtle = Color(0xFF3F4247)
)

/** Warm cream and terracotta palettes in the style of Claude's interface. */
val ZeroConClaudeDarkPalette = ZeroConPalette(
    isDark = true,
    canvas = Color(0xFF262624),
    surface = Color(0xFF2B2B29),
    surfaceElevated = Color(0xFF3A3936),
    keyNormal = Color(0xFF3A3936),
    keyModifier = Color(0xFF302F2D),
    primaryAccent = Color(0xFFD97757),
    primaryAccentSoft = Color(0xFFE39A80),
    onAccent = Color(0xFF1F1E1D),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF87171),
    textPrimary = Color(0xFFE5E3DA),
    textSecondary = Color(0xFFB0ADA2),
    textMuted = Color(0xFF9A978C),
    borderSubtle = Color(0xFF47463F)
)

val ZeroConClaudeLightPalette = ZeroConPalette(
    isDark = false,
    canvas = Color(0xFFF0EEE6),
    surface = Color(0xFFFAF9F5),
    surfaceElevated = Color(0xFFE6E3D9),
    keyNormal = Color(0xFFFAF9F5),
    keyModifier = Color(0xFFE3E0D5),
    primaryAccent = Color(0xFFB85A3B),
    primaryAccentSoft = Color(0xFFC96442),
    onAccent = Color(0xFFFFFFFF),
    readyEmerald = Color(0xFF047857),
    warningAmber = Color(0xFFB45309),
    danger = Color(0xFFB91C1C),
    textPrimary = Color(0xFF3D3929),
    textSecondary = Color(0xFF6B675C),
    textMuted = Color(0xFF7A766A),
    borderSubtle = Color(0xFFD6D2C4)
)

val ZeroConForestPalette = ZeroConPalette(
    isDark = true,
    canvas = Color(0xFF101E19),
    surface = Color(0xFF182C23),
    surfaceElevated = Color(0xFF254234),
    keyNormal = Color(0xFF254234),
    keyModifier = Color(0xFF1D3429),
    primaryAccent = Color(0xFF80D5A5),
    primaryAccentSoft = Color(0xFFA4E0BE),
    onAccent = Color(0xFF102A1B),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF87171),
    textPrimary = Color(0xFFE7F5ED),
    textSecondary = Color(0xFFA2C4B1),
    textMuted = Color(0xFF90B09F),
    borderSubtle = Color(0xFF355943)
)

val ZeroConPlumPalette = ZeroConPalette(
    isDark = true,
    canvas = Color(0xFF211727),
    surface = Color(0xFF302139),
    surfaceElevated = Color(0xFF47314F),
    keyNormal = Color(0xFF47314F),
    keyModifier = Color(0xFF382741),
    primaryAccent = Color(0xFFD6A0ED),
    primaryAccentSoft = Color(0xFFE1BAF2),
    onAccent = Color(0xFF30143D),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF87171),
    textPrimary = Color(0xFFF5EAFA),
    textSecondary = Color(0xFFC5ABCF),
    textMuted = Color(0xFFB199BB),
    borderSubtle = Color(0xFF604269)
)

val ZeroConSakuraPalette = ZeroConPalette(
    isDark = false,
    canvas = Color(0xFFFFF5F7),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF9E4EB),
    keyNormal = Color(0xFFFFFFFF),
    keyModifier = Color(0xFFF9E4EB),
    primaryAccent = Color(0xFFA72E5B),
    primaryAccentSoft = Color(0xFFB75479),
    onAccent = Color(0xFFFFFFFF),
    readyEmerald = Color(0xFF047857),
    warningAmber = Color(0xFFB45309),
    danger = Color(0xFFB91C1C),
    textPrimary = Color(0xFF442631),
    textSecondary = Color(0xFF815763),
    textMuted = Color(0xFF906A75),
    borderSubtle = Color(0xFFE2B6C5)
)

/**
 * Mirrors the Windows settings "paper" light palette.
 */
val ZeroConPaperPalette = ZeroConPalette(
    isDark = false,
    canvas = Color(0xFFF7F5EF),
    surface = Color(0xFFFFFEFA),
    surfaceElevated = Color(0xFFEAE6DB),
    keyNormal = Color(0xFFFFFEFA),
    keyModifier = Color(0xFFEAE6DB),
    primaryAccent = Color(0xFF716027),
    primaryAccentSoft = Color(0xFF8A7834),
    onAccent = Color(0xFFFFFFFF),
    readyEmerald = Color(0xFF047857),
    warningAmber = Color(0xFFB45309),
    danger = Color(0xFFB91C1C),
    textPrimary = Color(0xFF302E27),
    textSecondary = Color(0xFF6E6756),
    textMuted = Color(0xFF7A7260),
    borderSubtle = Color(0xFFD3CBB7)
)

/**
 * Android-only "standard" palettes modelled on the stock Android keyboard look: neutral
 * grey keys on a slightly darker canvas, a blue enter key, and no decoration.
 */
val ZeroConStandardDarkPalette = ZeroConPalette(
    isDark = true,
    canvas = Color(0xFF292E32),
    surface = Color(0xFF292E32),
    surfaceElevated = Color(0xFF3C4145),
    keyNormal = Color(0xFF484C4F),
    keyModifier = Color(0xFF373C40),
    primaryAccent = Color(0xFF5F97F6),
    primaryAccentSoft = Color(0xFF8AB4F8),
    onAccent = Color(0xFF0A1A33),
    readyEmerald = Color(0xFF34D399),
    warningAmber = Color(0xFFFBBF24),
    danger = Color(0xFFF28B82),
    textPrimary = Color(0xFFE8EAED),
    textSecondary = Color(0xFFBDC1C6),
    textMuted = Color(0xFF9AA0A6),
    borderSubtle = Color(0xFF4A4F53)
)

val ZeroConStandardLightPalette = ZeroConPalette(
    isDark = false,
    canvas = Color(0xFFE8EAED),
    surface = Color(0xFFE8EAED),
    surfaceElevated = Color(0xFFF8F9FA),
    keyNormal = Color(0xFFFFFFFF),
    keyModifier = Color(0xFFD3D7DB),
    primaryAccent = Color(0xFF1A73E8),
    primaryAccentSoft = Color(0xFF1967D2),
    onAccent = Color(0xFFFFFFFF),
    readyEmerald = Color(0xFF047857),
    warningAmber = Color(0xFFB45309),
    danger = Color(0xFFB91C1C),
    textPrimary = Color(0xFF202124),
    textSecondary = Color(0xFF5F6368),
    textMuted = Color(0xFF6B7075),
    borderSubtle = Color(0xFFC4C7CA)
)

/** Kept for callers that only need the default dark palette. */
val ZeroConDarkPalette = ZeroConStandardDarkPalette

/** Kept for callers that only need the default light palette. */
val ZeroConLightPalette = ZeroConStandardLightPalette

const val ZERO_CON_THEME_SYSTEM = "system"
const val ZERO_CON_THEME_DEFAULT_DARK = "standard_dark"
const val ZERO_CON_THEME_DEFAULT_LIGHT = "standard_light"

/**
 * Selectable themes in display order: the Android-only standard pair first, then the six
 * palettes shared with the Windows settings (four dark, two light).
 */
val ZeroConThemes: List<ZeroConTheme> = listOf(
    ZeroConTheme("standard_dark", "スタンダード", ZeroConStandardDarkPalette),
    ZeroConTheme("standard_light", "スタンダード（ライト）", ZeroConStandardLightPalette),
    ZeroConTheme("dark_blue", "ダークブルー", ZeroConDarkBluePalette),
    ZeroConTheme("graphite", "グラファイト", ZeroConGraphitePalette),
    ZeroConTheme("forest", "フォレスト", ZeroConForestPalette),
    ZeroConTheme("plum", "プラム", ZeroConPlumPalette),
    ZeroConTheme("sakura", "サクラ", ZeroConSakuraPalette),
    ZeroConTheme("paper", "ペーパー", ZeroConPaperPalette),
    ZeroConTheme("claude_dark", "クロード", ZeroConClaudeDarkPalette),
    ZeroConTheme("claude_light", "クロード（ライト）", ZeroConClaudeLightPalette)
)

fun zeroConThemeById(id: String): ZeroConTheme? = ZeroConThemes.firstOrNull { it.id == id }

/**
 * Resolves the stored theme setting to a concrete theme id.
 * "system" follows the device dark mode; legacy "dark"/"light" values from
 * earlier builds map to the default dark and light palettes.
 */
fun resolveZeroConThemeId(themeMode: String, systemDark: Boolean): String = when (themeMode) {
    ZERO_CON_THEME_SYSTEM, "" -> if (systemDark) ZERO_CON_THEME_DEFAULT_DARK else ZERO_CON_THEME_DEFAULT_LIGHT
    "dark" -> ZERO_CON_THEME_DEFAULT_DARK
    "light" -> ZERO_CON_THEME_DEFAULT_LIGHT
    else -> if (zeroConThemeById(themeMode) != null) themeMode else ZERO_CON_THEME_DEFAULT_DARK
}

fun resolveZeroConTheme(themeMode: String, systemDark: Boolean): ZeroConTheme =
    zeroConThemeById(resolveZeroConThemeId(themeMode, systemDark)) ?: ZeroConThemes.first()

@Composable
fun rememberZeroConPalette(themeMode: String = ZERO_CON_THEME_SYSTEM): ZeroConPalette =
    resolveZeroConTheme(themeMode, isSystemInDarkTheme()).palette

fun ZeroConPalette.toColorScheme(): ColorScheme = if (isDark) {
    darkColorScheme(
        primary = primaryAccent,
        onPrimary = onAccent,
        primaryContainer = primaryAccent.copy(alpha = 0.18f),
        onPrimaryContainer = textPrimary,
        secondary = surfaceElevated,
        onSecondary = textPrimary,
        background = canvas,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfaceElevated,
        onSurfaceVariant = textSecondary,
        outline = borderSubtle,
        error = danger,
        onError = Color(0xFF1F0A0A)
    )
} else {
    lightColorScheme(
        primary = primaryAccent,
        onPrimary = onAccent,
        primaryContainer = primaryAccent.copy(alpha = 0.12f),
        onPrimaryContainer = textPrimary,
        secondary = surfaceElevated,
        onSecondary = textPrimary,
        background = canvas,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfaceElevated,
        onSurfaceVariant = textSecondary,
        outline = borderSubtle,
        error = danger,
        onError = Color.White
    )
}

fun zeroConTypography(): Typography {
    val sans = FontFamily.SansSerif
    return Typography(
        titleLarge = TextStyle(
            fontFamily = sans,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp
        ),
        titleMedium = TextStyle(
            fontFamily = sans,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold
        ),
        bodyLarge = TextStyle(
            fontFamily = sans,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = sans,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        bodySmall = TextStyle(
            fontFamily = sans,
            fontSize = 12.sp,
            lineHeight = 17.sp
        ),
        labelLarge = TextStyle(
            fontFamily = sans,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        ),
        labelMedium = TextStyle(
            fontFamily = sans,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        labelSmall = TextStyle(
            fontFamily = sans,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

/** The launcher icon's glyph on its cream tile, so the settings header matches the home screen. */
@Composable
fun ZeroConLogo(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(colorResource(R.color.ic_launcher_background)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zerocon_logo_glyph),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.74f)
        )
    }
}
