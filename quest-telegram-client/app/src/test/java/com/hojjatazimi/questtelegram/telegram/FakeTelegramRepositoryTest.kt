package com.hojjatazimi.questtelegram.telegram

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeTelegramRepositoryTest {
    @Test
    fun authFlowLoadsChatsAndSendsMessages() = runTest {
        val repository = FakeTelegramRepository()

        assertEquals(AuthState.Uninitialized, repository.authState.value)

        repository.initialize()
        assertEquals(AuthState.WaitingForPhoneNumber, repository.authState.value)

        repository.submitPhoneNumber("+15551234567")
        assertEquals(AuthState.WaitingForCode, repository.authState.value)

        repository.submitAuthCode("12345")
        assertEquals(AuthState.Ready, repository.authState.value)

        repository.loadChats()
        assertEquals(3, repository.chats.value.size)
        assertEquals(ChatListState.Loaded(isEmpty = false), repository.chatListState.value)

        val chatId = repository.chats.value.first().id
        repository.openChat(chatId)
        val initialMessageCount = repository.currentMessages.value.size
        assertTrue(initialMessageCount > 0)
        assertEquals(ChatMessagesState.Loaded(chatId, isEmpty = false), repository.currentMessagesState.value)

        repository.sendTextMessage(chatId, "Testing from fake mode")
        val sentMessage = repository.currentMessages.value.last()
        assertEquals(initialMessageCount + 1, repository.currentMessages.value.size)
        assertEquals("Testing from fake mode", sentMessage.text)
        assertTrue(sentMessage.isOutgoing)
        assertEquals(MessageStatus.Sent, sentMessage.status)
        assertEquals(ChatMessagesState.Loaded(chatId, isEmpty = false), repository.currentMessagesState.value)
        assertEquals("Testing from fake mode", repository.chats.value.first { it.id == chatId }.lastMessage)
    }

    @Test
    fun invalidFakeAuthInputsExposeErrorsWithoutLoadingData() = runTest {
        val repository = FakeTelegramRepository()

        repository.initialize()
        repository.submitPhoneNumber("12")

        assertTrue(repository.authState.value is AuthState.Error)
        assertTrue(repository.chats.value.isEmpty())
        assertTrue(repository.currentMessages.value.isEmpty())
    }

    @Test
    fun logoutClearsFakeState() = runTest {
        val repository = FakeTelegramRepository()

        repository.initialize()
        repository.submitPhoneNumber("+15551234567")
        repository.submitAuthCode("12345")
        repository.loadChats()
        repository.openChat(repository.chats.value.first().id)

        repository.logout()

        assertEquals(AuthState.WaitingForPhoneNumber, repository.authState.value)
        assertTrue(repository.chats.value.isEmpty())
        assertEquals(ChatListState.Idle, repository.chatListState.value)
        assertTrue(repository.currentMessages.value.isEmpty())
        assertEquals(ChatMessagesState.Idle, repository.currentMessagesState.value)
    }

    @Test
    fun authSubmissionsExposeInFlightStates() = runTest {
        val repository = FakeTelegramRepository()
        val states = mutableListOf<AuthState>()

        val job = launch {
            repository.authState.collect { states += it }
        }

        repository.initialize()
        repository.submitPhoneNumber("+15551234567")
        repository.submitAuthCode("12345")

        assertTrue(states.contains(AuthState.SubmittingPhoneNumber))
        assertTrue(states.contains(AuthState.SubmittingCode))
        assertEquals(AuthState.Ready, repository.authState.value)

        job.cancel()
    }

    @Test
    fun reopeningFakeChatUsesCachedMessages() = runTest {
        val repository = FakeTelegramRepository()

        repository.initialize()
        repository.submitPhoneNumber("+15551234567")
        repository.submitAuthCode("12345")
        repository.loadChats()

        val chatId = repository.chats.value.first().id
        repository.openChat(chatId)
        assertEquals(ChatMessagesState.Loaded(chatId, isEmpty = false), repository.currentMessagesState.value)

        repository.openChat(chatId)
        assertEquals(ChatMessagesState.Loaded(chatId, isEmpty = false), repository.currentMessagesState.value)
    }
}
