package com.chatmen.data.repository.chat

import com.chatmen.data.model.Chat
import com.chatmen.data.model.Message
import com.chatmen.data.model.User
import com.chatmen.data.response.ChatDto
import com.mongodb.client.result.InsertOneResult

interface ChatRepository {

    suspend fun getChatsForUser(username: String): List<ChatDto>

    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message>

    suspend fun getChatById(chatId: String): Chat?

    suspend fun insertChat(
        members: List<String>,
        name: String? = null,
        lastMessageId: String? = null,
        chatIconUrl: String? = null
    ): InsertOneResult

    suspend fun getMembersOfChat(chatId: String): List<User>

    suspend fun insertMessage(message: Message)

    suspend fun updateLastMessageForChat(chatId: String, lastMessageId: String)

    suspend fun doesChatByUsersExist(users: List<String>): Boolean
}