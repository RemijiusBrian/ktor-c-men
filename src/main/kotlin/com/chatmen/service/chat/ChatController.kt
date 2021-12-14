package com.chatmen.service.chat

import com.chatmen.data.model.UnreadMessage
import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.websocket.WsClientMessage
import com.chatmen.data.websocket.WsServerMessage
import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import org.bson.types.ObjectId
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val chatRepository: ChatRepository,
    private val gson: Gson
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
    suspend fun sendMessage(username: String, frameText: String) {

        val message = gson.fromJson(frameText, WsClientMessage::class.java)
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

        // Save Unread If Any Members Offline
        saveUnreadIfAnyMembersOffline(chat.members, messageId)

        val wsJsonMessage = gson.toJson(wsServerMessage)
        chat.members.forEach { member ->
            onlineMembers[member]?.send(Frame.Text(wsJsonMessage))
        }

        // Update Last Message For Chat
        chatRepository.updateLastMessageForChat(chat.id, messageEntity.id)
    }

    // Save Unread If Any Members Offline
    private suspend fun saveUnreadIfAnyMembersOffline(chatMembers: List<String>, messageId: String) {
        val membersOffline = chatMembers.filter { !onlineMembers.containsKey(it) }
        if (membersOffline.isNotEmpty()) {
            chatRepository.insertOrUpdateUnreadMessage(
                UnreadMessage(messageId, membersOffline)
            )
        }
    }

    suspend fun sendUnreadMessagesIfAny(member: String) {
        val unreadMessages = chatRepository.getUnreadMessagesForMember(member)

        if (unreadMessages.isEmpty()) return

        unreadMessages.forEach { message ->
            val frameText = gson.toJson(message.toWsServerMessage())
            onlineMembers[member]?.send(frameText)
        }
    }
}