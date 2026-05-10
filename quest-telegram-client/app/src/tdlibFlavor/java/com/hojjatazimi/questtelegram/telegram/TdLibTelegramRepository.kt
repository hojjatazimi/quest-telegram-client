package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TdLibTelegramRepository(
    config: TelegramConfig,
    appContext: Context,
) : TelegramRepository, TdLibClient.Listener {
    private val client = TdLibClient(appContext, config, this)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatSummary>>(emptyList())
    override val chats: StateFlow<List<ChatSummary>> = _chats.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<MessageItem>>(emptyList())
    override val currentMessages: StateFlow<List<MessageItem>> = _currentMessages.asStateFlow()

    private var activeChatId: Long? = null

    override suspend fun initialize() {
        runCatching {
            client.initialize()
        }.onFailure { error ->
            _authState.value = AuthState.Error(error.message ?: "TDLib initialization failed.")
        }
    }

    override suspend fun submitPhoneNumber(phone: String) {
        val cleanPhone = phone.trim()
        val digitCount = cleanPhone.count { it.isDigit() }
        if (!cleanPhone.startsWith("+") || digitCount < 8) {
            _authState.value = AuthState.Error("Use international phone format, starting with + and country code.")
            return
        }
        _authState.value = AuthState.SubmittingPhoneNumber
        client.submitPhoneNumber(cleanPhone)
    }

    override suspend fun submitAuthCode(code: String) {
        _authState.value = AuthState.SubmittingCode
        client.submitAuthCode(code)
    }

    override suspend fun submitPassword(password: String) {
        _authState.value = AuthState.SubmittingPassword
        client.submitPassword(password)
    }

    override suspend fun loadChats() {
        client.loadChats()
    }

    override suspend fun openChat(chatId: Long) {
        activeChatId = chatId
        client.openChat(chatId)
    }

    override suspend fun sendTextMessage(chatId: Long, text: String) {
        client.sendTextMessage(chatId, text)
    }

    override suspend fun logout() {
        _authState.value = AuthState.LoggingOut
        client.closeAndClearSession()
        activeChatId = null
        _chats.value = emptyList()
        _currentMessages.value = emptyList()
    }

    override fun onAuthState(authState: AuthState) {
        _authState.value = authState
    }

    override fun onChats(chats: List<ChatSummary>) {
        _chats.value = chats
    }

    override fun onMessages(chatId: Long, messages: List<MessageItem>) {
        if (activeChatId == chatId) {
            _currentMessages.value = messages
        }
    }

    override fun onMessageAdded(chatId: Long, message: MessageItem) {
        if (activeChatId == chatId) {
            _currentMessages.value = (_currentMessages.value + message).distinctBy { it.id }.sortedBy { it.id }
        }
    }

    override fun onMessageUpdated(chatId: Long, message: MessageItem) {
        if (activeChatId == chatId) {
            _currentMessages.value = _currentMessages.value
                .map { if (it.id == message.id) message else it }
                .sortedBy { it.id }
        }
    }

    override fun onError(message: String) {
        _authState.value = AuthState.Error(message)
    }
}
