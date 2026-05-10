package com.hojjatazimi.questtelegram.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hojjatazimi.questtelegram.QuestTelegramViewModel
import com.hojjatazimi.questtelegram.telegram.AuthState
import com.hojjatazimi.questtelegram.ui.screens.ChatListScreen
import com.hojjatazimi.questtelegram.ui.screens.ChatScreen
import com.hojjatazimi.questtelegram.ui.screens.LoginScreen

@Composable
fun AppNavigation(viewModel: QuestTelegramViewModel) {
    val navController = rememberNavController()
    val authState by viewModel.authState.collectAsState()
    var lastLoginStep by remember { mutableStateOf<AuthState>(AuthState.WaitingForPhoneNumber) }

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.WaitingForPhoneNumber,
            AuthState.WaitingForCode,
            AuthState.WaitingForPassword,
            AuthState.SubmittingPhoneNumber,
            AuthState.SubmittingCode,
            AuthState.SubmittingPassword,
            -> lastLoginStep = authState
            else -> Unit
        }

        when (authState) {
            AuthState.Ready -> {
                viewModel.loadChats()
                navController.navigate(Routes.ChatList) {
                    popUpTo(Routes.Login) { inclusive = true }
                    launchSingleTop = true
                }
            }
            AuthState.WaitingForPhoneNumber,
            is AuthState.Error,
            AuthState.Closed,
            -> navController.navigate(Routes.Login) {
                popUpTo(0)
                launchSingleTop = true
            }
            else -> Unit
        }
    }

    NavHost(navController = navController, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginScreen(
                authState = authState,
                lastLoginStep = lastLoginStep,
                onSubmitPhone = viewModel::submitPhoneNumber,
                onSubmitCode = viewModel::submitCode,
                onSubmitPassword = viewModel::submitPassword,
            )
        }
        composable(Routes.ChatList) {
            val chats by viewModel.chats.collectAsState()
            val chatListState by viewModel.chatListState.collectAsState()
            ChatListScreen(
                chats = chats,
                chatListState = chatListState,
                onOpenChat = { chatId ->
                    viewModel.openChat(chatId)
                    navController.navigate(Routes.chat(chatId))
                },
                onRefresh = viewModel::loadChats,
                onLogout = viewModel::logout,
            )
        }
        composable(
            route = Routes.Chat,
            arguments = listOf(navArgument("chatId") { type = NavType.LongType }),
        ) { entry ->
            val chatId = entry.arguments?.getLong("chatId") ?: return@composable
            val chats by viewModel.chats.collectAsState()
            val messages by viewModel.currentMessages.collectAsState()
            val messagesState by viewModel.currentMessagesState.collectAsState()
            ChatScreen(
                chat = chats.firstOrNull { it.id == chatId },
                chats = chats,
                currentChatId = chatId,
                messages = messages,
                messagesState = messagesState,
                onBack = { navController.popBackStack() },
                onOpenChat = { nextChatId ->
                    viewModel.openChat(nextChatId)
                    navController.navigate(Routes.chat(nextChatId)) {
                        popUpTo(Routes.ChatList)
                        launchSingleTop = true
                    }
                },
                onSend = { text -> viewModel.sendTextMessage(chatId, text) },
            )
        }
    }
}
