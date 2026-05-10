package com.hojjatazimi.questtelegram.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hojjatazimi.questtelegram.telegram.ChatSummary
import com.hojjatazimi.questtelegram.telegram.MessageItem
import com.hojjatazimi.questtelegram.ui.components.ChatRow
import com.hojjatazimi.questtelegram.ui.components.MessageBubble
import com.hojjatazimi.questtelegram.ui.components.QuestTextField

@Composable
fun ChatScreen(
    chat: ChatSummary?,
    chats: List<ChatSummary>,
    currentChatId: Long,
    messages: List<MessageItem>,
    onBack: () -> Unit,
    onOpenChat: (Long) -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by remember { mutableStateOf("") }
    val messageListState = rememberLazyListState()

    LaunchedEffect(currentChatId, messages.size) {
        if (messages.isNotEmpty()) {
            messageListState.animateScrollToItem(messages.lastIndex)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 42.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 1240.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text("Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TeleQuest",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = chat?.title ?: "Conversation",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .widthIn(max = 1240.dp)
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                tonalElevation = 5.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.34f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(chats, key = { it.id }) { item ->
                            ChatRow(
                                chat = item,
                                selected = item.id == currentChatId,
                                onClick = { onOpenChat(item.id) },
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .weight(0.66f)
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.62f),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            ConversationHeader(chat = chat)
                            LazyColumn(
                                state = messageListState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                if (messages.isEmpty()) {
                                    item {
                                        EmptyConversationState()
                                    }
                                } else {
                                    items(messages, key = { it.id }) { message ->
                                        MessageBubble(message = message)
                                    }
                                }
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    QuestTextField(
                                        value = draft,
                                        onValueChange = { draft = it },
                                        label = "Message",
                                        modifier = Modifier.weight(1f),
                                    )
                                    Button(
                                        onClick = {
                                            val text = draft
                                            draft = ""
                                            onSend(text)
                                        },
                                        enabled = draft.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.height(64.dp),
                                    ) {
                                        Text(text = "Send", style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyConversationState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 46.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "No messages loaded yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Telegram may still be syncing this conversation.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ConversationHeader(chat: ChatSummary?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat?.title ?: "Conversation",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Ready",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        ) {
            Text(
                text = chat?.timestamp.orEmpty(),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    Spacer(Modifier.height(2.dp))
}
