package com.hojjatazimi.questtelegram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hojjatazimi.questtelegram.config.TelegramConfig
import com.hojjatazimi.questtelegram.telegram.FakeTelegramRepository
import com.hojjatazimi.questtelegram.telegram.TdLibTelegramRepository
import com.hojjatazimi.questtelegram.telegram.TelegramRepository
import com.hojjatazimi.questtelegram.ui.theme.QuestTelegramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = createRepository()
        setContent {
            QuestTelegramTheme {
                QuestTelegramApp(repository = repository)
            }
        }
    }

    private fun createRepository(): TelegramRepository {
        return when (BuildConfig.TELEGRAM_BACKEND) {
            "tdlib" -> TdLibTelegramRepository(
                config = TelegramConfig.fromBuildEnvironment(applicationContext),
                appContext = applicationContext,
            )
            else -> FakeTelegramRepository()
        }
    }
}
