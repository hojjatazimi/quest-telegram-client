package com.hojjatazimi.questtelegram.navigation

object Routes {
    const val Login = "login"
    const val ChatList = "chat_list"
    const val Chat = "chat/{chatId}"

    fun chat(chatId: Long): String = "chat/$chatId"
}
