package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TdLibTelegramRepository(
    config: TelegramConfig,
    appContext: Context,
) : TelegramRepository {
    private val client = TdLibClient(appContext, config)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatSummary>>(emptyList())
    override val chats: StateFlow<List<ChatSummary>> = _chats.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<MessageItem>>(emptyList())
    override val currentMessages: StateFlow<List<MessageItem>> = _currentMessages.asStateFlow()

    override suspend fun initialize() {
        runCatching {
            client.initialize()
        }.onSuccess {
            // TODO: Map authorizationStateWaitTdlibParameters after passing TDLib parameters.
            // TODO: Map authorizationStateWaitPhoneNumber to AuthState.WaitingForPhoneNumber.
            // TODO: Map authorizationStateWaitCode to AuthState.WaitingForCode.
            // TODO: Map authorizationStateWaitPassword to AuthState.WaitingForPassword.
            // TODO: Map authorizationStateReady to AuthState.Ready.
            // TODO: Map authorizationStateLoggingOut to AuthState.LoggingOut.
            // TODO: Map authorizationStateClosed to AuthState.Closed.
            _authState.value = AuthState.WaitingForPhoneNumber
        }.onFailure { error ->
            _authState.value = AuthState.Error(error.message ?: "TDLib initialization failed.")
        }
    }

    override suspend fun submitPhoneNumber(phone: String) {
        // Never log phone numbers, auth codes, passwords, messages, api_hash, session details, or TDLib payloads.
        _authState.value = AuthState.WaitingForCode
        // TODO: Send SetAuthenticationPhoneNumber to TDLib.
    }

    override suspend fun submitAuthCode(code: String) {
        _authState.value = AuthState.Ready
        // TODO: Send CheckAuthenticationCode to TDLib.
    }

    override suspend fun submitPassword(password: String) {
        _authState.value = AuthState.Ready
        // TODO: Send CheckAuthenticationPassword to TDLib.
    }

    override suspend fun loadChats() {
        // TODO: Load main chat list through TDLib and map chats into ChatSummary.
    }

    override suspend fun openChat(chatId: Long) {
        // TODO: Open chat, load message history, and publish mapped MessageItem values.
        _currentMessages.value = emptyList()
    }

    override suspend fun sendTextMessage(chatId: Long, text: String) {
        // TODO: Send inputMessageText through TDLib and expose Sending/Sent/Failed updates.
    }

    override suspend fun logout() {
        _authState.value = AuthState.LoggingOut
        client.closeAndClearSession()
        // TODO: Use encrypted storage for any future local account metadata and clear it here.
        _chats.value = emptyList()
        _currentMessages.value = emptyList()
        _authState.value = AuthState.Closed
    }
}
