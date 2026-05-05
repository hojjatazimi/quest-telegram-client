package com.hojjatazimi.questtelegram.telegram

data class ChatSummary(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isMuted: Boolean,
)

data class MessageItem(
    val id: Long,
    val chatId: Long,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val status: MessageStatus,
)

enum class MessageStatus {
    Sending,
    Sent,
    Failed,
    Read,
}
