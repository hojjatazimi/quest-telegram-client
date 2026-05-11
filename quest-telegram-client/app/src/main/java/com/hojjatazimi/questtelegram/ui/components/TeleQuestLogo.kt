package com.hojjatazimi.questtelegram.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hojjatazimi.questtelegram.R

@Composable
fun TeleQuestLogo(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.64f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.telequest_launcher_icon),
                contentDescription = "TeleQuest",
                modifier = Modifier
                    .size(size)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
