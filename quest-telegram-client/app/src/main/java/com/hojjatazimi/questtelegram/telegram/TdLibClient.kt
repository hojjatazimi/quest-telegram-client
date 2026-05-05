package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig

class TdLibClient(
    private val context: Context,
    private val config: TelegramConfig,
) {
    fun initialize() {
        check(config.hasDeveloperCredentials) {
            "TDLib builds require TELEGRAM_API_ID and TELEGRAM_API_HASH in non-committed configuration."
        }
        // TODO: Load TDLib JNI/native binaries when they are added to the tdlib flavor.
        // TODO: Store TDLib database and downloaded files under context.filesDir, not shared storage.
        // TODO: Pass api_id/api_hash from TelegramConfig without logging or exposing them.
        // TODO: Subscribe to TDLib updates and map authorization/chat/message updates into app models.
        @Suppress("UNUSED_VARIABLE")
        val privateFilesDir = context.filesDir
    }

    suspend fun closeAndClearSession() {
        // TODO: Call TDLib logout/close and remove app-private tdlib database/files after confirmation.
    }
}
