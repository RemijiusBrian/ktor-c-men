package com.chatmen.data.repository.chat

import com.chatmen.data.model.Chat
import com.chatmen.data.model.Member
import com.chatmen.data.model.Message
import com.chatmen.data.model.UnreadMessage
import com.chatmen.data.response.ChatDto
import com.mongodb.client.result.InsertOneResult
import org.bson.types.ObjectId

interface ChatRepository {

    suspend fun getChatsForMember(member: String): List<ChatDto>

    suspend fun getChatById(chatId: String): Chat?

    suspend fun insertChat(
        members: List<String>,
        name: String? = null,
        lastMessageId: String? = null,
        chatIconUrl: String? = null,
        description: String? = null,
        id: String = ObjectId().toString()
    ): InsertOneResult

    suspend fun getMembersOfChat(chatId: String): List<Member>

    suspend fun insertMessage(message: Message)

    suspend fun updateLastMessageForChat(chatId: String, lastMessageId: String)

    suspend fun doesChatByMembersExist(members: List<String>): Boolean

    suspend fun insertOrUpdateUnreadMessage(unreadMessage: UnreadMessage)

    suspend fun getUnreadMessagesForMember(member: String): List<Message>

    suspend fun removeMemberFromUnread(unreadMessage: String, member: String)
}