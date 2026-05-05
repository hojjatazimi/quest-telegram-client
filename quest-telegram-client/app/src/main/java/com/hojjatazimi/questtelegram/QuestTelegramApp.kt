package com.hojjatazimi.questtelegram

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hojjatazimi.questtelegram.navigation.AppNavigation
import com.hojjatazimi.questtelegram.telegram.TelegramRepository

@Composable
fun QuestTelegramApp(repository: TelegramRepository) {
    val viewModel: QuestTelegramViewModel = viewModel(
        factory = QuestTelegramViewModelFactory(repository),
    )
    AppNavigation(viewModel = viewModel)
}

class QuestTelegramViewModelFactory(
    private val repository: TelegramRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestTelegramViewModel::class.java)) {
            return QuestTelegramViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
