package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig

class TdLibClient(
    private val context: Context,
    private val config: TelegramConfig,
) {
    private var initialized = false

    fun initialize() {
        check(config.hasDeveloperCredentials) {
            "TDLib builds require TELEGRAM_API_ID and TELEGRAM_API_HASH in non-committed configuration."
        }
        val paths = tdLibPaths()
        // TODO: Load TDLib JNI/native binaries from the tdlib flavor before constructing the client.
        // TODO: Create TDLib client and send SetTdlibParameters using paths.databasePath and paths.filesPath.
        // TODO: Pass api_id/api_hash from TelegramConfig without logging, displaying, or persisting them.
        // TODO: Subscribe to TDLib updates and map authorization/chat/message updates into repository state.
        @Suppress("UNUSED_VARIABLE")
        val tdLibPaths = paths
        initialized = true
    }

    fun tdLibPaths(): TdLibPaths {
        val tdLibRoot = context.filesDir.resolve("tdlib")
        return TdLibPaths(
            databasePath = tdLibRoot.resolve("database").absolutePath,
            filesPath = tdLibRoot.resolve("files").absolutePath,
        )
    }

    suspend fun submitPhoneNumber(phone: String) {
        ensureInitialized()
        @Suppress("UNUSED_PARAMETER")
        val redactedPhone = phone
        // TODO: Send SetAuthenticationPhoneNumber. Never log or display the phone number.
    }

    suspend fun submitAuthCode(code: String) {
        ensureInitialized()
        @Suppress("UNUSED_PARAMETER")
        val redactedCode = code
        // TODO: Send CheckAuthenticationCode. Never log or persist auth codes.
    }

    suspend fun submitPassword(password: String) {
        ensureInitialized()
        @Suppress("UNUSED_PARAMETER")
        val redactedPassword = password
        // TODO: Send CheckAuthenticationPassword. Never log or persist passwords.
    }

    suspend fun loadChats() {
        ensureInitialized()
        // TODO: Request the main chat list and map TDLib chat updates to ChatSummary.
    }

    suspend fun openChat(chatId: Long) {
        ensureInitialized()
        @Suppress("UNUSED_PARAMETER")
        val tdLibChatId = chatId
        // TODO: Open the chat and request message history into MessageItem values.
    }

    suspend fun sendTextMessage(chatId: Long, text: String) {
        ensureInitialized()
        @Suppress("UNUSED_PARAMETER")
        val messageTarget = chatId to text
        // TODO: Send inputMessageText and publish Sending/Sent/Failed status changes.
    }

    suspend fun closeAndClearSession() {
        // TODO: Call TDLib logout/close and remove app-private tdlib database/files after confirmation.
        initialized = false
    }

    private fun ensureInitialized() {
        check(initialized) { "TDLib client is not initialized." }
    }
}

data class TdLibPaths(
    val databasePath: String,
    val filesPath: String,
)
