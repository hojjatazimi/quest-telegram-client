package com.hojjatazimi.questtelegram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkQuestColors = darkColorScheme(
    primary = Color(0xFF9FE870),
    onPrimary = Color(0xFF102018),
    secondary = Color(0xFF86D1FF),
    onSecondary = Color(0xFF0B1B26),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE8EEF2),
    surface = Color(0xFF182028),
    onSurface = Color(0xFFE8EEF2),
    surfaceVariant = Color(0xFF26313A),
    onSurfaceVariant = Color(0xFFCCD7DF),
)

private val LightQuestColors = lightColorScheme(
    primary = Color(0xFF176B45),
    onPrimary = Color.White,
    secondary = Color(0xFF00668B),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF101418),
    surface = Color.White,
    onSurface = Color(0xFF101418),
    surfaceVariant = Color(0xFFE6EDF2),
    onSurfaceVariant = Color(0xFF33414B),
)

@Composable
fun QuestTelegramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkQuestColors else LightQuestColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
