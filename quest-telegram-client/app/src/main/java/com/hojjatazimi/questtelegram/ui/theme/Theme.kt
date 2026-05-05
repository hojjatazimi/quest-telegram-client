package com.hojjatazimi.questtelegram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkQuestColors = darkColorScheme(
    primary = Color(0xFFA7E8C2),
    onPrimary = Color(0xFF102019),
    secondary = Color(0xFF8FD5D8),
    onSecondary = Color(0xFF102123),
    tertiary = Color(0xFFE9C98B),
    background = Color(0xFF0D1114),
    onBackground = Color(0xFFE9F0EE),
    surface = Color(0xFF171D21),
    onSurface = Color(0xFFE9F0EE),
    surfaceVariant = Color(0xFF20292E),
    onSurfaceVariant = Color(0xFFB9C7C4),
    outline = Color(0xFF354247),
)

private val LightQuestColors = lightColorScheme(
    primary = Color(0xFF28684B),
    onPrimary = Color.White,
    secondary = Color(0xFF326D70),
    tertiary = Color(0xFF89651C),
    background = Color(0xFFF2F5F3),
    onBackground = Color(0xFF111716),
    surface = Color.White,
    onSurface = Color(0xFF111716),
    surfaceVariant = Color(0xFFE1E9E6),
    onSurfaceVariant = Color(0xFF40504D),
    outline = Color(0xFFC4D0CC),
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
