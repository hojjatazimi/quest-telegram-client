package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig

object TelegramRepositoryProvider {
    fun create(appContext: Context): TelegramRepository {
        return TdLibTelegramRepository(
            config = TelegramConfig.fromBuildEnvironment(appContext),
            appContext = appContext.applicationContext,
        )
    }
}
