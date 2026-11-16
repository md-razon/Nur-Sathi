package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.ThemePreference

@Immutable
data class NoorSathiExtraColors(
    val heroStart: Color,
    val heroEnd: Color,
    val goldAccent: Color,
    val goldSoft: Color,
    val cardBorder: Color,
    val highlightSurface: Color,
    val onHero: Color,
    val onHeroMuted: Color,
)

val LocalNoorSathiColors = staticCompositionLocalOf {
    NoorSathiExtraColors(
        heroStart = EmeraldPrimary,
        heroEnd = EmeraldDeep,
        goldAccent = GoldPrimary,
        goldSoft = GoldSoft,
        cardBorder = CreamBorder,
        highlightSurface = EmeraldSoft,
        onHero = CreamCard,
        onHeroMuted = GoldLight,
    )
}

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = CreamCard,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = EmeraldDeep,
    secondary = GoldDark,
    onSecondary = CreamCard,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = EmeraldDeep,
    tertiary = EmeraldMedium,
    onTertiary = CreamCard,
    background = CreamBackground,
    onBackground = LightTextPrimary,
    surface = CreamCard,
    onSurface = LightTextPrimary,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = CreamBorder,
    outlineVariant = CreamBorder.copy(alpha = 0.6f),
    error = LightError,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGoldAccent,
    onPrimary = EmeraldDeep,
    primaryContainer = MidnightCardElevated,
    onPrimaryContainer = ChampagneText,
    secondary = EmeraldBright,
    onSecondary = ChampagneText,
    secondaryContainer = MidnightCard,
    onSecondaryContainer = GoldLight,
    tertiary = GoldPrimary,
    onTertiary = EmeraldDeep,
    background = MidnightBg,
    onBackground = ChampagneText,
    surface = MidnightSurface,
    onSurface = ChampagneText,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = ChampagneMuted,
    outline = MidnightBorder,
    outlineVariant = MidnightBorder.copy(alpha = 0.6f),
    error = DarkError,
    onError = EmeraldDeep,
)

val NoorSathiShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

@Composable
fun NoorSathiTheme(
    themePreference: ThemePreference = ThemePreference.LIGHT,
    content: @Composable () -> Unit,
) {
    val isDark = when (themePreference) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    val targetScheme = if (isDark) DarkColorScheme else LightColorScheme
    val animSpec = tween<Color>(durationMillis = 320)

    val animatedScheme = ColorScheme(
        primary = animateColorAsState(targetScheme.primary, animSpec, label = "primary").value,
        onPrimary = animateColorAsState(targetScheme.onPrimary, animSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(targetScheme.primaryContainer, animSpec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(targetScheme.onPrimaryContainer, animSpec, label = "onPrimaryContainer").value,
        inversePrimary = targetScheme.inversePrimary,
        secondary = animateColorAsState(targetScheme.secondary, animSpec, label = "secondary").value,
        onSecondary = animateColorAsState(targetScheme.onSecondary, animSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(targetScheme.secondaryContainer, animSpec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(targetScheme.onSecondaryContainer, animSpec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(targetScheme.tertiary, animSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(targetScheme.onTertiary, animSpec, label = "onTertiary").value,
        tertiaryContainer = targetScheme.tertiaryContainer,
        onTertiaryContainer = targetScheme.onTertiaryContainer,
        background = animateColorAsState(targetScheme.background, animSpec, label = "background").value,
        onBackground = animateColorAsState(targetScheme.onBackground, animSpec, label = "onBackground").value,
        surface = animateColorAsState(targetScheme.surface, animSpec, label = "surface").value,
        onSurface = animateColorAsState(targetScheme.onSurface, animSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(targetScheme.surfaceVariant, animSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(targetScheme.onSurfaceVariant, animSpec, label = "onSurfaceVariant").value,
        surfaceTint = targetScheme.surfaceTint,
        inverseSurface = targetScheme.inverseSurface,
        inverseOnSurface = targetScheme.inverseOnSurface,
        error = targetScheme.error,
        onError = targetScheme.onError,
        errorContainer = targetScheme.errorContainer,
        onErrorContainer = targetScheme.onErrorContainer,
        outline = animateColorAsState(targetScheme.outline, animSpec, label = "outline").value,
        outlineVariant = animateColorAsState(targetScheme.outlineVariant, animSpec, label = "outlineVariant").value,
        scrim = targetScheme.scrim,
        surfaceBright = targetScheme.surfaceBright,
        surfaceDim = targetScheme.surfaceDim,
        surfaceContainer = animateColorAsState(targetScheme.surfaceVariant, animSpec, label = "surfaceContainer").value,
        surfaceContainerHigh = animateColorAsState(targetScheme.surface, animSpec, label = "surfaceContainerHigh").value,
        surfaceContainerHighest = animateColorAsState(targetScheme.surface, animSpec, label = "surfaceContainerHighest").value,
        surfaceContainerLow = animateColorAsState(targetScheme.background, animSpec, label = "surfaceContainerLow").value,
        surfaceContainerLowest = animateColorAsState(targetScheme.background, animSpec, label = "surfaceContainerLowest").value,
    )

    val extraColors = if (isDark) {
        NoorSathiExtraColors(
            heroStart = MidnightCardElevated,
            heroEnd = MidnightBg,
            goldAccent = DarkGoldAccent,
            goldSoft = MidnightCardElevated,
            cardBorder = MidnightBorder,
            highlightSurface = MidnightCardElevated,
            onHero = ChampagneText,
            onHeroMuted = DarkGoldAccent,
        )
    } else {
        NoorSathiExtraColors(
            heroStart = EmeraldMedium,
            heroEnd = EmeraldDeep,
            goldAccent = GoldPrimary,
            goldSoft = GoldSoft,
            cardBorder = CreamBorder,
            highlightSurface = EmeraldSoft,
            onHero = CreamCard,
            onHeroMuted = GoldLight,
        )
    }

    CompositionLocalProvider(LocalNoorSathiColors provides extraColors) {
        MaterialTheme(
            colorScheme = animatedScheme,
            typography = NoorSathiTypography,
            shapes = NoorSathiShapes,
            content = content,
        )
    }
}
