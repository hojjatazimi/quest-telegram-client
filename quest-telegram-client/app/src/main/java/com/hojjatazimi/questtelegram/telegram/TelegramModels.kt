package com.hojjatazimi.questtelegram.telegram

data class ChatSummary(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isMuted: Boolean,
    val avatarPhotoPath: String? = null,
    val presenceText: String? = null,
)

sealed class ChatListState {
    data object Idle : ChatListState()
    data class Loading(val loadedCount: Int, val targetCount: Int) : ChatListState()
    data class Loaded(val isEmpty: Boolean) : ChatListState()
    data class Error(val message: String) : ChatListState()
}

sealed class ChatMessagesState {
    data object Idle : ChatMessagesState()
    data class Loading(val chatId: Long) : ChatMessagesState()
    data class Loaded(val chatId: Long, val isEmpty: Boolean) : ChatMessagesState()
    data class Error(val chatId: Long, val message: String) : ChatMessagesState()
}

data class MessageItem(
    val id: Long,
    val chatId: Long,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val status: MessageStatus,
    val seenText: String? = null,
)

enum class MessageStatus {
    Sending,
    Sent,
    Failed,
    Read,
}
