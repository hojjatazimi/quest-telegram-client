package com.hojjatazimi.questtelegram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object TeleQuestLiquidGlass {
    val QuestCyan = Color(0xFF8FEFFF)
    val IceBlue = Color(0xFFDDF2FF)
    val SkySignal = Color(0xFF4FAFEA)

    val FrostWhite = Color(0xFFF7FCFF)
    val GlassMist = Color(0xB8EAF6FD)
    val CloudGlass = Color(0xD1D6EAF7)

    val GlassFill = Color(0x75DDF2FF)
    val GlassRim = Color(0xD9CDEEFF)
    val GlassHighlight = Color(0xEBFFFFFF)
    val GlassShadow = Color(0x47A8C7DA)

    val TextPrimary = Color(0xFF102A43)
    val TextSecondary = Color(0xFF6B8498)
    val TextMuted = Color(0xFF9AAFC0)

    val Success = Color(0xFF5EE6B5)
    val Warning = Color(0xFFFFD166)
    val Error = Color(0xFFFF6B8A)
    val Info = Color(0xFF73C9FF)

    val DarkBackground = Color(0xFF071622)
    val DarkSurface = Color(0xFF102A3A)
    val DarkGlow = Color(0xFF143449)
    val DarkGlassSurface = Color(0x7A23465A)
    val DarkTextPrimary = Color(0xFFF2FAFF)
    val DarkTextSecondary = Color(0xFFA8C7DA)
    val DarkBorder = Color(0x478FEFFF)

    fun brandGradient(): Brush = Brush.linearGradient(
        colors = listOf(IceBlue, QuestCyan, SkySignal),
    )

    fun liquidSurfaceGradient(): Brush = Brush.linearGradient(
        colors = listOf(GlassHighlight, GlassFill, QuestCyan.copy(alpha = 0.22f)),
    )

    @Composable
    fun appBackgroundBrush(darkTheme: Boolean = isSystemInDarkTheme()): Brush {
        return if (darkTheme) {
            Brush.linearGradient(
                colors = listOf(DarkBackground, DarkSurface, DarkGlow),
            )
        } else {
            liquidSurfaceGradient()
        }
    }
}

private val DarkQuestColors = darkColorScheme(
    primary = TeleQuestLiquidGlass.QuestCyan,
    onPrimary = TeleQuestLiquidGlass.TextPrimary,
    primaryContainer = TeleQuestLiquidGlass.DarkGlassSurface,
    onPrimaryContainer = TeleQuestLiquidGlass.DarkTextPrimary,
    secondary = TeleQuestLiquidGlass.SkySignal,
    onSecondary = TeleQuestLiquidGlass.TextPrimary,
    tertiary = TeleQuestLiquidGlass.Success,
    background = TeleQuestLiquidGlass.DarkBackground,
    onBackground = TeleQuestLiquidGlass.DarkTextPrimary,
    surface = TeleQuestLiquidGlass.DarkSurface,
    onSurface = TeleQuestLiquidGlass.DarkTextPrimary,
    surfaceVariant = TeleQuestLiquidGlass.DarkGlassSurface,
    onSurfaceVariant = TeleQuestLiquidGlass.DarkTextSecondary,
    outline = TeleQuestLiquidGlass.DarkBorder,
    error = TeleQuestLiquidGlass.Error,
    onError = TeleQuestLiquidGlass.TextPrimary,
)

private val LightQuestColors = lightColorScheme(
    primary = TeleQuestLiquidGlass.QuestCyan,
    onPrimary = TeleQuestLiquidGlass.TextPrimary,
    primaryContainer = TeleQuestLiquidGlass.IceBlue,
    onPrimaryContainer = TeleQuestLiquidGlass.TextPrimary,
    secondary = TeleQuestLiquidGlass.SkySignal,
    onSecondary = TeleQuestLiquidGlass.TextPrimary,
    tertiary = TeleQuestLiquidGlass.Success,
    background = TeleQuestLiquidGlass.FrostWhite,
    onBackground = TeleQuestLiquidGlass.TextPrimary,
    surface = TeleQuestLiquidGlass.GlassMist,
    onSurface = TeleQuestLiquidGlass.TextPrimary,
    surfaceVariant = TeleQuestLiquidGlass.CloudGlass,
    onSurfaceVariant = TeleQuestLiquidGlass.TextSecondary,
    outline = TeleQuestLiquidGlass.GlassRim,
    error = TeleQuestLiquidGlass.Error,
    onError = TeleQuestLiquidGlass.TextPrimary,
)

private val TeleQuestTypography = Typography(
    displaySmall = TextStyle(fontSize = 40.sp, lineHeight = 48.sp, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 27.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium),
)

private val TeleQuestShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
)

@Composable
fun QuestTelegramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkQuestColors else LightQuestColors,
        typography = TeleQuestTypography,
        shapes = TeleQuestShapes,
        content = content,
    )
}
