package com.hojjatazimi.questtelegram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hojjatazimi.questtelegram.telegram.TelegramRepositoryProvider
import com.hojjatazimi.questtelegram.ui.theme.QuestTelegramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TelegramRepositoryProvider.create(applicationContext)
        setContent {
            QuestTelegramTheme {
                QuestTelegramApp(repository = repository)
            }
        }
    }
}
