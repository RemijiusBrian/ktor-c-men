package com.chatmen.service.chat

import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.websocket.WsClientMessage
import com.chatmen.data.websocket.WsServerMessage
import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import org.bson.types.ObjectId
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val chatRepository: ChatRepository,
) {
    private val onlineMembers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(username: String, socket: WebSocketSession) {
        onlineMembers[username] = socket
    }

    fun onDisconnect(username: String) {
        if (onlineMembers.containsKey(username))
            onlineMembers.remove(username)
    }

    @Throws(InsufficientMembersException::class, ChatCreationException::class)
    suspend fun sendMessage(
        username: String,
        message: WsClientMessage,
        gson: Gson
    ) {
        if (message.members.isEmpty()) throw InsufficientMembersException()

        var newMessage: WsClientMessage = message

        if (message.chatId.isNullOrBlank()) {
            val newChatId = ObjectId().toString()
            val wasAcknowledged = chatRepository.insertChat(
                members = mutableListOf<String>().apply {
                    add(username)
                    addAll(message.members)
                },
                id = newChatId
            ).wasAcknowledged()

            if (!wasAcknowledged) throw ChatCreationException()

            newMessage = message.copy(
                chatId = newChatId
            )
        }

        val chat = chatRepository.getChatById(newMessage.chatId!!) ?: throw ChatCreationException()

        val messageId = ObjectId().toString()
        val messageEntity = newMessage.toMessage(username, chat.id, messageId)
        chatRepository.insertMessage(messageEntity)

        val wsServerMessage = WsServerMessage(
            messageId = messageId,
            fromUsername = username,
            text = message.text,
            timestamp = System.currentTimeMillis(),
            chatId = chat.id
        )

        val frameText = gson.toJson(wsServerMessage)
        chat.members.forEach { member ->
            onlineMembers[member]?.send(Frame.Text(frameText))
        }

        // Update Last Message For Chat
        chatRepository.updateLastMessageForChat(chat.id, messageEntity.id)
    }
}