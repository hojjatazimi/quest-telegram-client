package com.hojjatazimi.questtelegram.config

import android.content.Context
import com.hojjatazimi.questtelegram.BuildConfig

data class TelegramConfig(
    val apiId: Int?,
    val apiHash: String?,
) {
    val hasDeveloperCredentials: Boolean
        get() = apiId != null && !apiHash.isNullOrBlank()

    companion object {
        fun fromBuildEnvironment(context: Context): TelegramConfig {
            @Suppress("UNUSED_PARAMETER")
            val unusedContext = context
            return TelegramConfig(
                apiId = BuildConfig.TELEGRAM_API_ID.takeIf { it > 0 },
                apiHash = BuildConfig.TELEGRAM_API_HASH.takeIf { it.isNotBlank() },
            )
        }
    }
}
