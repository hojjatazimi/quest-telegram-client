package com.hojjatazimi.questtelegram.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hojjatazimi.questtelegram.telegram.ChatListState
import com.hojjatazimi.questtelegram.telegram.ChatSummary
import com.hojjatazimi.questtelegram.ui.components.ChatRow
import com.hojjatazimi.questtelegram.ui.components.TeleQuestLogo
import com.hojjatazimi.questtelegram.ui.theme.TeleQuestLiquidGlass

@Composable
fun ChatListScreen(
    chats: List<ChatSummary>,
    chatListState: ChatListState,
    onOpenChat: (Long) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TeleQuestLiquidGlass.appBackgroundBrush())
                .padding(horizontal = 42.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 1240.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TeleQuestLogo(size = 58.dp)
                    Column {
                        Text(
                            text = "TeleQuest",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Inbox",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                OutlinedButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .height(56.dp)
                        .padding(end = 12.dp),
                ) {
                    Text(text = "Refresh")
                }
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(text = "Log out")
                }
            }
            Surface(
                modifier = Modifier
                    .widthIn(max = 1240.dp)
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.74f)),
                tonalElevation = 2.dp,
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (chats.isEmpty()) {
                            item {
                                StatePanel(
                                    chatListState = chatListState,
                                    onRefresh = onRefresh,
                                    compact = true,
                                )
                            }
                        } else {
                            when (val state = chatListState) {
                                is ChatListState.Loading -> item {
                                    ListStatusCard(text = "Fetching chats ${state.loadedCount}/${state.targetCount}")
                                }
                                is ChatListState.Error -> item {
                                    ListStatusCard(text = state.message)
                                }
                                else -> Unit
                            }
                            items(chats, key = { it.id }) { chat ->
                                ChatRow(chat = chat, onClick = { onOpenChat(chat.id) })
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.62f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (chats.isEmpty()) {
                                StatePanel(chatListState = chatListState, onRefresh = onRefresh)
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    Text(
                                        text = "Select a conversation",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (chatListState is ChatListState.Loading) {
                                        ChatFetchProgress(chatListState = chatListState)
                                    }
                                    Text(
                                        text = "TeleQuest keeps the message surface wide and relaxed in headset.",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun ChatFetchProgress(chatListState: ChatListState.Loading) {
    val progress = (chatListState.loadedCount.toFloat() / chatListState.targetCount.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    Column(
        modifier = Modifier.widthIn(max = 360.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Fetching chats ${chatListState.loadedCount}/${chatListState.targetCount}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatePanel(
    chatListState: ChatListState,
    onRefresh: () -> Unit,
    compact: Boolean = false,
) {
    val title = when (chatListState) {
        ChatListState.Idle -> "No chats loaded"
        is ChatListState.Loading -> "Loading chats..."
        is ChatListState.Loaded -> if (chatListState.isEmpty) "No chats yet" else "Chats loaded"
        is ChatListState.Error -> "Could not load chats"
    }
    val detail = when (chatListState) {
        ChatListState.Idle -> "Refresh to load your Telegram chats."
        is ChatListState.Loading -> "Fetched ${chatListState.loadedCount} of ${chatListState.targetCount} requested chats."
        is ChatListState.Loaded -> if (chatListState.isEmpty) {
            "No conversations are available in the main chat list yet."
        } else {
            "Select a conversation from the list."
        }
        is ChatListState.Error -> chatListState.message
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.62f)),
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 20.dp else 30.dp),
            horizontalAlignment = if (compact) Alignment.Start else Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (chatListState is ChatListState.Loading) {
                ChatFetchProgress(chatListState = chatListState)
            }
            if (chatListState !is ChatListState.Loading) {
                OutlinedButton(
                    onClick = onRefresh,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(text = "Refresh")
                }
            }
        }
    }
}

@Composable
private fun ListStatusCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.62f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
