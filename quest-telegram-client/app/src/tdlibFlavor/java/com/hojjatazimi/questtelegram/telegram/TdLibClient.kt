package com.hojjatazimi.questtelegram.telegram

import android.content.Context
import com.hojjatazimi.questtelegram.config.TelegramConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi

class TdLibClient(
    context: Context,
    private val config: TelegramConfig,
    private val listener: Listener,
) {
    companion object {
        init {
            System.loadLibrary("tdjni")
        }
    }

    interface Listener {
        fun onAuthState(authState: AuthState)
        fun onChats(chats: List<ChatSummary>)
        fun onMessages(chatId: Long, messages: List<MessageItem>)
        fun onMessageAdded(chatId: Long, message: MessageItem)
        fun onMessageUpdated(chatId: Long, message: MessageItem)
        fun onMessageLoadError(chatId: Long, message: String)
        fun onError(message: String)
    }

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val chats = ConcurrentHashMap<Long, TdApi.Chat>()
    private val users = ConcurrentHashMap<Long, TdApi.User>()
    private val messagesByChat = ConcurrentHashMap<Long, MutableList<MessageItem>>()
    private val avatarFileChatIds = ConcurrentHashMap<Int, MutableSet<Long>>()
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val weekdayFormat = SimpleDateFormat("EEE", Locale.US)
    private val dateFormat = SimpleDateFormat("MMM d", Locale.US)

    @Volatile
    private var client: Client? = null

    @Volatile
    private var currentChatId: Long? = null

    fun initialize() {
        check(config.hasDeveloperCredentials) {
            "TDLib builds require TELEGRAM_API_ID and TELEGRAM_API_HASH in non-committed configuration."
        }

        runCatching {
            Client.execute(TdApi.SetLogVerbosityLevel(1))
            client = Client.create(
                { update -> handleUpdate(update) },
                { error -> listener.onError("TDLib exception: ${error.message ?: "unknown error"}") },
                { error -> listener.onError("TDLib default error: ${error.message ?: "unknown error"}") },
            )
        }.onFailure {
            listener.onError("TDLib failed to start. Confirm generated TDLib sources and libtdjni.so are installed.")
        }
    }

    fun tdLibPaths(): TdLibPaths {
        val tdLibRoot = appContext.filesDir.resolve("tdlib")
        return TdLibPaths(
            databasePath = tdLibRoot.resolve("database").absolutePath,
            filesPath = tdLibRoot.resolve("files").absolutePath,
        )
    }

    fun submitPhoneNumber(phone: String) {
        val cleanPhone = phone.trim()
        sendAuth(
            TdApi.SetAuthenticationPhoneNumber(cleanPhone, null),
            "Failed to submit phone number.",
        )
    }

    fun submitAuthCode(code: String) {
        sendAuth(TdApi.CheckAuthenticationCode(code), "Failed to submit auth code.")
    }

    fun submitPassword(password: String) {
        sendAuth(TdApi.CheckAuthenticationPassword(password), "Failed to submit password.")
    }

    fun loadChats(limit: Int = 100) {
        client?.send(TdApi.LoadChats(TdApi.ChatListMain(), limit)) { result ->
            when (result.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = result as TdApi.Error
                    if (error.code != 404) {
                        listener.onError("Failed to load chats.")
                    }
                    emitChats()
                }
                TdApi.Ok.CONSTRUCTOR -> emitChats()
            }
        }
        emitChats()
    }

    fun openChat(chatId: Long, limit: Int = 50) {
        currentChatId = chatId
        client?.send(TdApi.OpenChat(chatId), silentHandler())
        client?.send(TdApi.GetChatHistory(chatId, 0, 0, limit, false)) { result ->
            when (result.constructor) {
                TdApi.Messages.CONSTRUCTOR -> {
                    val messages = (result as TdApi.Messages).messages
                        .mapNotNull(::mapMessage)
                        .sortedBy { it.id }
                    messagesByChat[chatId] = messages.toMutableList()
                    listener.onMessages(chatId, messages)
                }
                TdApi.Error.CONSTRUCTOR -> listener.onMessageLoadError(chatId, "Failed to load messages.")
            }
        }
    }

    fun sendTextMessage(chatId: Long, text: String) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val content = TdApi.InputMessageText(
            TdApi.FormattedText(cleanText, null),
            null,
            true,
        )
        client?.send(TdApi.SendMessage(chatId, null, null, null, null, content)) { result ->
            when (result.constructor) {
                TdApi.Message.CONSTRUCTOR -> mapMessage(result as TdApi.Message)?.let { message ->
                    upsertMessage(chatId, message)
                }
                TdApi.Error.CONSTRUCTOR -> listener.onMessageLoadError(chatId, "Failed to send message.")
            }
        }
    }

    fun closeAndClearSession() {
        client?.send(TdApi.LogOut(), silentHandler())
        currentChatId = null
        chats.clear()
        messagesByChat.clear()
    }

    private fun handleUpdate(update: TdApi.Object) {
        when (update.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                val state = (update as TdApi.UpdateAuthorizationState).authorizationState
                handleAuthorizationState(state)
            }
            TdApi.UpdateNewChat.CONSTRUCTOR -> {
                val chat = (update as TdApi.UpdateNewChat).chat
                chats[chat.id] = chat
                emitChats()
            }
            TdApi.UpdateChatTitle.CONSTRUCTOR -> {
                val updateTitle = update as TdApi.UpdateChatTitle
                chats[updateTitle.chatId]?.title = updateTitle.title
                emitChats()
            }
            TdApi.UpdateChatPhoto.CONSTRUCTOR -> {
                val photo = update as TdApi.UpdateChatPhoto
                chats[photo.chatId]?.photo = photo.photo
                emitChats()
            }
            TdApi.UpdateChatLastMessage.CONSTRUCTOR -> {
                val lastMessage = update as TdApi.UpdateChatLastMessage
                chats[lastMessage.chatId]?.let { chat ->
                    chat.lastMessage = lastMessage.lastMessage
                    chat.positions = lastMessage.positions
                }
                emitChats()
            }
            TdApi.UpdateChatPosition.CONSTRUCTOR -> {
                val positionUpdate = update as TdApi.UpdateChatPosition
                chats[positionUpdate.chatId]?.let { chat ->
                    chat.positions = updateChatPositions(chat.positions, positionUpdate.position)
                }
                emitChats()
            }
            TdApi.UpdateChatDraftMessage.CONSTRUCTOR -> {
                val draft = update as TdApi.UpdateChatDraftMessage
                chats[draft.chatId]?.positions = draft.positions
                emitChats()
            }
            TdApi.UpdateChatNotificationSettings.CONSTRUCTOR -> {
                val settings = update as TdApi.UpdateChatNotificationSettings
                chats[settings.chatId]?.notificationSettings = settings.notificationSettings
                emitChats()
            }
            TdApi.UpdateChatReadInbox.CONSTRUCTOR -> {
                val readInbox = update as TdApi.UpdateChatReadInbox
                chats[readInbox.chatId]?.unreadCount = readInbox.unreadCount
                emitChats()
            }
            TdApi.UpdateChatReadOutbox.CONSTRUCTOR -> {
                val readOutbox = update as TdApi.UpdateChatReadOutbox
                chats[readOutbox.chatId]?.lastReadOutboxMessageId = readOutbox.lastReadOutboxMessageId
                refreshMessageReadStates(readOutbox.chatId, readOutbox.lastReadOutboxMessageId)
            }
            TdApi.UpdateUser.CONSTRUCTOR -> {
                val user = (update as TdApi.UpdateUser).user
                users[user.id] = user
                emitChats()
            }
            TdApi.UpdateUserStatus.CONSTRUCTOR -> {
                val status = update as TdApi.UpdateUserStatus
                users[status.userId]?.status = status.status
                emitChats()
            }
            TdApi.UpdateFile.CONSTRUCTOR -> {
                val file = (update as TdApi.UpdateFile).file
                handleUpdatedFile(file)
            }
            TdApi.UpdateNewMessage.CONSTRUCTOR -> {
                val message = mapMessage((update as TdApi.UpdateNewMessage).message) ?: return
                upsertMessage(message.chatId, message)
            }
            TdApi.UpdateMessageSendSucceeded.CONSTRUCTOR -> {
                val message = mapMessage((update as TdApi.UpdateMessageSendSucceeded).message) ?: return
                upsertMessage(message.chatId, message.copy(status = MessageStatus.Sent))
            }
            TdApi.UpdateMessageSendFailed.CONSTRUCTOR -> {
                val failed = update as TdApi.UpdateMessageSendFailed
                val message = mapMessage(failed.message) ?: return
                upsertMessage(message.chatId, message.copy(status = MessageStatus.Failed))
            }
            TdApi.UpdateMessageInteractionInfo.CONSTRUCTOR -> {
                val interaction = update as TdApi.UpdateMessageInteractionInfo
                val existing = messagesByChat[interaction.chatId]?.firstOrNull { it.id == interaction.messageId } ?: return
                val status = if (interaction.interactionInfo?.viewCount?.let { it > 0 } == true) {
                    MessageStatus.Read
                } else {
                    existing.status
                }
                val updated = existing.copy(status = status, seenText = seenText(status, interaction.interactionInfo?.viewCount))
                upsertMessage(interaction.chatId, updated)
            }
        }
    }

    private fun handleAuthorizationState(state: TdApi.AuthorizationState) {
        when (state.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> sendTdLibParameters()
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> listener.onAuthState(AuthState.WaitingForPhoneNumber)
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> listener.onAuthState(AuthState.WaitingForCode)
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> listener.onAuthState(AuthState.WaitingForPassword)
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                listener.onAuthState(AuthState.Ready)
                loadChats()
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> listener.onAuthState(AuthState.LoggingOut)
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> listener.onAuthState(AuthState.Closed)
            TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR -> {
                listener.onError("Confirm this login from another Telegram device, then return to TeleQuest.")
            }
            TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR -> {
                listener.onError("New account registration is not supported in this MVP.")
            }
            else -> listener.onError("Unsupported Telegram authorization state.")
        }
    }

    private fun sendTdLibParameters() {
        val paths = tdLibPaths()
        appContext.filesDir.resolve("tdlib").mkdirs()

        val request = TdApi.SetTdlibParameters()
        request.databaseDirectory = paths.databasePath
        request.filesDirectory = paths.filesPath
        request.useFileDatabase = true
        request.useChatInfoDatabase = true
        request.useMessageDatabase = true
        request.useSecretChats = false
        request.apiId = config.apiId ?: 0
        request.apiHash = config.apiHash.orEmpty()
        request.systemLanguageCode = Locale.getDefault().language.ifBlank { "en" }
        request.deviceModel = "Meta Quest 3"
        request.systemVersion = "Meta Horizon OS"
        request.applicationVersion = "0.1.0"

        sendAuth(request, "Failed to configure TDLib.")
    }

    private fun sendAuth(function: TdApi.Function<out TdApi.Object>, fallbackMessage: String) {
        client?.send(function) { result ->
            if (result.constructor == TdApi.Error.CONSTRUCTOR) {
                listener.onError("$fallbackMessage ${safeErrorMessage(result as TdApi.Error)}")
            }
        } ?: listener.onError("TDLib is not initialized.")
    }

    private fun safeErrorMessage(error: TdApi.Error): String {
        val message = error.message
            ?.takeIf { it.isNotBlank() }
            ?.replace(Regex("[+]?\\d[\\d\\s().-]{5,}"), "[redacted]")
            ?: "TDLib error ${error.code}."
        return "($message)"
    }

    private fun emitChats() {
        val summaries = chats.values
            .sortedWith(
                compareByDescending<TdApi.Chat> { mainListOrder(it) }
                    .thenByDescending { it.id }
                    .thenBy { it.title.lowercase(Locale.US) },
            )
            .map(::mapChat)
        listener.onChats(summaries)
    }

    private fun updateChatPositions(
        currentPositions: Array<TdApi.ChatPosition>?,
        newPosition: TdApi.ChatPosition,
    ): Array<TdApi.ChatPosition> {
        val updatedPositions = currentPositions
            .orEmpty()
            .filterNot { it.list.constructor == newPosition.list.constructor }
            .toMutableList()

        if (newPosition.order != 0L) {
            updatedPositions += newPosition
        }

        return updatedPositions.toTypedArray()
    }

    private fun mapChat(chat: TdApi.Chat): ChatSummary {
        return ChatSummary(
            id = chat.id,
            title = chat.title.ifBlank { "Telegram chat" },
            lastMessage = chat.lastMessage?.let(::messagePreview).orEmpty(),
            timestamp = chat.lastMessage?.date?.let(::formatUnixTime).orEmpty(),
            unreadCount = chat.unreadCount,
            isMuted = chat.notificationSettings?.muteFor != 0,
            avatarPhotoPath = avatarPhotoPath(chat),
            presenceText = presenceText(chat),
        )
    }

    private fun mapMessage(message: TdApi.Message): MessageItem? {
        val status = messageStatus(message)
        return MessageItem(
            id = message.id,
            chatId = message.chatId,
            senderName = senderName(message),
            text = messagePreview(message),
            timestamp = formatUnixTime(message.date),
            isOutgoing = message.isOutgoing,
            status = status,
            seenText = seenText(status, message.interactionInfo?.viewCount),
        )
    }

    private fun avatarPhotoPath(chat: TdApi.Chat): String? {
        val smallPhoto = chat.photo?.small ?: return null
        avatarFileChatIds.getOrPut(smallPhoto.id) { ConcurrentHashMap.newKeySet() }.add(chat.id)
        return if (smallPhoto.local?.isDownloadingCompleted == true && smallPhoto.local?.path?.isNotBlank() == true) {
            smallPhoto.local.path
        } else {
            client?.send(TdApi.DownloadFile(smallPhoto.id, 8, 0, 0, false), silentHandler())
            null
        }
    }

    private fun presenceText(chat: TdApi.Chat): String? {
        return when (val type = chat.type) {
            is TdApi.ChatTypePrivate -> users[type.userId]?.status?.let(::userStatusText)
            is TdApi.ChatTypeBasicGroup -> "group"
            is TdApi.ChatTypeSupergroup -> "group"
            is TdApi.ChatTypeSecret -> "secret chat"
            else -> null
        }
    }

    private fun userStatusText(status: TdApi.UserStatus): String {
        return when (status) {
            is TdApi.UserStatusOnline -> "online"
            is TdApi.UserStatusOffline -> "last seen ${formatUnixTime(status.wasOnline)}"
            is TdApi.UserStatusRecently -> "last seen recently"
            is TdApi.UserStatusLastWeek -> "last seen last week"
            is TdApi.UserStatusLastMonth -> "last seen last month"
            else -> "last seen unavailable"
        }
    }

    private fun upsertMessage(chatId: Long, message: MessageItem) {
        val messages = messagesByChat.getOrPut(chatId) { mutableListOf() }
        val index = messages.indexOfFirst { it.id == message.id }
        if (index >= 0) {
            messages[index] = message
            listener.onMessageUpdated(chatId, message)
        } else {
            messages.add(message)
            messages.sortBy { it.id }
            listener.onMessageAdded(chatId, message)
        }
        if (currentChatId == chatId) {
            listener.onMessages(chatId, messages.toList())
        }
    }

    private fun refreshMessageReadStates(chatId: Long, lastReadOutboxMessageId: Long) {
        val messages = messagesByChat[chatId] ?: return
        val updated = messages.map { message ->
            if (message.isOutgoing && message.id <= lastReadOutboxMessageId && message.status == MessageStatus.Sent) {
                message.copy(status = MessageStatus.Read, seenText = "seen")
            } else {
                message
            }
        }
        messages.clear()
        messages.addAll(updated)
        if (currentChatId == chatId) {
            listener.onMessages(chatId, updated)
        }
    }

    private fun handleUpdatedFile(file: TdApi.File) {
        val chatIds = avatarFileChatIds[file.id].orEmpty()
        if (chatIds.isEmpty()) return
        if (file.local?.isDownloadingCompleted == true) {
            chatIds.forEach { chatId ->
                chats[chatId]?.photo?.small = file
            }
            emitChats()
        }
    }

    private fun messagePreview(message: TdApi.Message): String {
        return when (val content = message.content) {
            is TdApi.MessageText -> content.text.text
            is TdApi.MessagePhoto -> content.caption.text.ifBlank { "[Photo]" }
            is TdApi.MessageVideo -> content.caption.text.ifBlank { "[Video]" }
            is TdApi.MessageDocument -> content.caption.text.ifBlank { "[Document]" }
            is TdApi.MessageAudio -> content.caption.text.ifBlank { "[Audio]" }
            is TdApi.MessageVoiceNote -> "[Voice message]"
            is TdApi.MessageSticker -> "[Sticker]"
            else -> "[Unsupported message]"
        }
    }

    private fun senderName(message: TdApi.Message): String {
        if (message.isOutgoing) return "You"
        return when (val sender = message.senderId) {
            is TdApi.MessageSenderUser -> users[sender.userId]?.let { user ->
                listOf(user.firstName, user.lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { "Telegram user" }
            } ?: "Telegram user"
            is TdApi.MessageSenderChat -> chats[sender.chatId]?.title ?: "Telegram chat"
            else -> "Telegram"
        }
    }

    private fun messageStatus(message: TdApi.Message): MessageStatus {
        val lastReadOutboxMessageId = chats[message.chatId]?.lastReadOutboxMessageId ?: 0L
        return when {
            message.sendingState is TdApi.MessageSendingStatePending -> MessageStatus.Sending
            message.sendingState is TdApi.MessageSendingStateFailed -> MessageStatus.Failed
            message.isOutgoing && lastReadOutboxMessageId >= message.id -> MessageStatus.Read
            message.isOutgoing && message.interactionInfo?.viewCount?.let { it > 0 } == true -> MessageStatus.Read
            else -> MessageStatus.Sent
        }
    }

    private fun seenText(status: MessageStatus, viewCount: Int?): String? {
        return when {
            viewCount != null && viewCount > 0 -> "$viewCount views"
            status == MessageStatus.Read -> "seen"
            status == MessageStatus.Sent -> "sent"
            status == MessageStatus.Sending -> "sending"
            status == MessageStatus.Failed -> "failed"
            else -> null
        }
    }

    private fun mainListOrder(chat: TdApi.Chat): Long {
        return chat.positions
            ?.firstOrNull { it.list.constructor == TdApi.ChatListMain.CONSTRUCTOR }
            ?.order
            ?: 0L
    }

    private fun formatUnixTime(seconds: Int): String {
        if (seconds <= 0) return ""
        val date = Date(seconds * 1000L)
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { time = date }
        return when {
            now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR) -> timeFormat.format(date)
            now.timeInMillis - then.timeInMillis < 7L * 24L * 60L * 60L * 1000L -> weekdayFormat.format(date)
            else -> dateFormat.format(date)
        }
    }

    private fun silentHandler(): Client.ResultHandler {
        return Client.ResultHandler { result ->
            if (result.constructor == TdApi.Error.CONSTRUCTOR) {
                scope.launch { listener.onError("Telegram request failed.") }
            }
        }
    }
}

data class TdLibPaths(
    val databasePath: String,
    val filesPath: String,
)
