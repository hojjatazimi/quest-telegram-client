package com.hojjatazimi.questtelegram.telegram

import kotlinx.coroutines.flow.StateFlow

interface TelegramRepository {
    val authState: StateFlow<AuthState>
    val chats: StateFlow<List<ChatSummary>>
    val chatListState: StateFlow<ChatListState>
    val currentMessages: StateFlow<List<MessageItem>>

    suspend fun initialize()
    suspend fun submitPhoneNumber(phone: String)
    suspend fun submitAuthCode(code: String)
    suspend fun submitPassword(password: String)
    suspend fun loadChats()
    suspend fun openChat(chatId: Long)
    suspend fun sendTextMessage(chatId: Long, text: String)
    suspend fun logout()
}
