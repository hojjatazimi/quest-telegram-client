package com.hojjatazimi.questtelegram.telegram

import android.content.Context

object TelegramRepositoryProvider {
    fun create(appContext: Context): TelegramRepository {
        @Suppress("UNUSED_PARAMETER")
        val unusedContext = appContext
        return FakeTelegramRepository()
    }
}
