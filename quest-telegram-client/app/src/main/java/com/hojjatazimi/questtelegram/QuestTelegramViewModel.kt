package com.hojjatazimi.questtelegram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hojjatazimi.questtelegram.telegram.TelegramRepository
import kotlinx.coroutines.launch

class QuestTelegramViewModel(
    val repository: TelegramRepository,
) : ViewModel() {
    val authState = repository.authState
    val chats = repository.chats
    val chatListState = repository.chatListState
    val currentMessages = repository.currentMessages

    init {
        viewModelScope.launch {
            repository.initialize()
        }
    }

    fun submitPhoneNumber(phone: String) {
        viewModelScope.launch {
            repository.submitPhoneNumber(phone)
        }
    }

    fun submitCode(code: String) {
        viewModelScope.launch {
            repository.submitAuthCode(code)
        }
    }

    fun submitPassword(password: String) {
        viewModelScope.launch {
            repository.submitPassword(password)
        }
    }

    fun loadChats() {
        viewModelScope.launch {
            repository.loadChats()
        }
    }

    fun openChat(chatId: Long) {
        viewModelScope.launch {
            repository.openChat(chatId)
        }
    }

    fun sendTextMessage(chatId: Long, text: String) {
        viewModelScope.launch {
            repository.sendTextMessage(chatId, text)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
