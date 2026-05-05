package com.hojjatazimi.questtelegram.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hojjatazimi.questtelegram.telegram.ChatSummary
import com.hojjatazimi.questtelegram.ui.components.ChatRow

@Composable
fun ChatListScreen(
    chats: List<ChatSummary>,
    onOpenChat: (Long) -> Unit,
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
                .padding(horizontal = 40.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 1180.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Fake repository mode",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(text = "Log out")
                }
            }
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 1180.dp)
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(chats, key = { it.id }) { chat ->
                    ChatRow(chat = chat, onClick = { onOpenChat(chat.id) })
                }
                if (chats.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.large,
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text(text = "No chats loaded", style = MaterialTheme.typography.titleLarge)
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.height(56.dp),
                                ) {
                                    Text("Waiting for repository")
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
