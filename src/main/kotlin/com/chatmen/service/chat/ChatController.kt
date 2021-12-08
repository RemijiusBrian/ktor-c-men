package com.chatmen.service.chat

import com.chatmen.data.model.Chat
import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.websocket.WsClientMessage
import com.chatmen.data.websocket.WsServerMessage
import com.chatmen.util.WebSocketObject
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val chatRepository: ChatRepository,
) {
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(username: String, socket: WebSocketSession) {
        onlineUsers[username] = socket
    }

    fun onDisconnect(username: String) {
        if (onlineUsers.containsKey(username))
            onlineUsers.remove(username)
    }

    @Throws(UserNotFoundException::class, ChatCreationException::class)
    suspend fun sendMessage(username: String, message: WsClientMessage) {
        var chat: Chat? = null
        if (message.chatId.isNullOrBlank()) {
            val insertedChatId = chatRepository.insertChat(
                members = listOf(
                    username,
                    message.toUsername
                )
            ).insertedId
            insertedChatId?.let { chatId ->
                chat = chatRepository.getChatById(chatId.toString())
            }
        }
        if (chat == null) throw ChatCreationException()

        val messageEntity = message.toMessage(username, chat!!.id)
        val wsServerMessage = WsServerMessage(
            fromUsername = username,
            toUsername = message.toUsername,
            text = message.text,
            timestamp = System.currentTimeMillis(),
            chatId = chat!!.id
        )

        chatRepository.insertMessage(messageEntity)

        val frameText = Json.encodeToString(wsServerMessage)
        chat!!.members.forEach { member ->
            onlineUsers[member]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))
        }

        // Update Last Message For Chat
        chatRepository.updateLastMessageForChat(chat!!.id, messageEntity.id)
    }
}