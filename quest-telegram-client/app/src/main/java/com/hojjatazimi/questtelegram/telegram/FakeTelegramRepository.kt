package com.hojjatazimi.questtelegram.telegram

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTelegramRepository : TelegramRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatSummary>>(emptyList())
    override val chats: StateFlow<List<ChatSummary>> = _chats.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<MessageItem>>(emptyList())
    override val currentMessages: StateFlow<List<MessageItem>> = _currentMessages.asStateFlow()

    private var activeChatId: Long? = null
    private var nextMessageId = 10_000L

    private val fakeChats = listOf(
        ChatSummary(
            id = 1,
            title = "Quest Dev Notes",
            lastMessage = "Try the wider chat surface in headset.",
            timestamp = "09:45",
            unreadCount = 2,
            isMuted = false,
        ),
        ChatSummary(
            id = 2,
            title = "Spatial App Crew",
            lastMessage = "The compose prototype feels readable at 2m.",
            timestamp = "Yesterday",
            unreadCount = 0,
            isMuted = false,
        ),
        ChatSummary(
            id = 3,
            title = "Build Pipeline",
            lastMessage = "TDLib binaries are still intentionally optional.",
            timestamp = "Mon",
            unreadCount = 4,
            isMuted = true,
        ),
    )

    private val messagesByChat = mutableMapOf(
        1L to listOf(
            incoming(1, 1, "Mina", "The Quest panel should breathe more than a phone UI.", "09:38"),
            outgoing(2, 1, "Agreed. I bumped the spacing and button targets.", "09:40"),
            incoming(3, 1, "Mina", "Try the wider chat surface in headset.", "09:45"),
        ),
        2L to listOf(
            incoming(4, 2, "Arman", "The compose prototype feels readable at 2m.", "Yesterday"),
            outgoing(5, 2, "Good. Next step is real auth behind the repo interface.", "Yesterday"),
        ),
        3L to listOf(
            incoming(6, 3, "CI", "Fake flavor build passes without TDLib dependencies.", "Mon"),
            incoming(7, 3, "CI", "TDLib binaries are still intentionally optional.", "Mon"),
        ),
    )

    override suspend fun initialize() {
        delay(250)
        _authState.value = AuthState.WaitingForPhoneNumber
    }

    override suspend fun submitPhoneNumber(phone: String) {
        if (phone.trim().length < 6) {
            _authState.value = AuthState.Error("Enter a full phone number for the fake sign-in flow.")
            return
        }
        _authState.value = AuthState.SubmittingPhoneNumber
        delay(350)
        _authState.value = AuthState.WaitingForCode
    }

    override suspend fun submitAuthCode(code: String) {
        if (code.trim().length < 3) {
            _authState.value = AuthState.Error("Enter any three or more digits to continue in fake mode.")
            return
        }
        _authState.value = AuthState.SubmittingCode
        delay(250)
        _authState.value = AuthState.Ready
    }

    override suspend fun submitPassword(password: String) {
        if (password.isBlank()) {
            _authState.value = AuthState.Error("Enter a password to continue in fake mode.")
            return
        }
        _authState.value = AuthState.SubmittingPassword
        delay(250)
        _authState.value = AuthState.Ready
    }

    override suspend fun loadChats() {
        _chats.value = fakeChats
    }

    override suspend fun openChat(chatId: Long) {
        activeChatId = chatId
        _currentMessages.value = messagesByChat[chatId].orEmpty()
    }

    override suspend fun sendTextMessage(chatId: Long, text: String) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val message = outgoing(
            id = nextMessageId++,
            chatId = chatId,
            text = cleanText,
            timestamp = "Now",
            status = MessageStatus.Sent,
        )
        val updated = messagesByChat[chatId].orEmpty() + message
        messagesByChat[chatId] = updated
        if (activeChatId == chatId) {
            _currentMessages.value = updated
        }
        _chats.value = _chats.value.map { chat ->
            if (chat.id == chatId) chat.copy(lastMessage = cleanText, timestamp = "Now", unreadCount = 0) else chat
        }
    }

    override suspend fun logout() {
        _authState.value = AuthState.LoggingOut
        delay(200)
        activeChatId = null
        _currentMessages.value = emptyList()
        _chats.value = emptyList()
        _authState.value = AuthState.WaitingForPhoneNumber
    }

    private fun incoming(
        id: Long,
        chatId: Long,
        senderName: String,
        text: String,
        timestamp: String,
    ) = MessageItem(id, chatId, senderName, text, timestamp, isOutgoing = false, status = MessageStatus.Read)

    private fun outgoing(
        id: Long,
        chatId: Long,
        text: String,
        timestamp: String,
        status: MessageStatus = MessageStatus.Read,
    ) = MessageItem(id, chatId, "You", text, timestamp, isOutgoing = true, status = status)
}
