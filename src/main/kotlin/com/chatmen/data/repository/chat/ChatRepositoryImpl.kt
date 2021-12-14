package com.chatmen.data.repository.chat

import com.chatmen.data.model.Chat
import com.chatmen.data.model.Member
import com.chatmen.data.model.Message
import com.chatmen.data.response.ChatDto
import com.mongodb.client.result.InsertOneResult
import org.litote.kmongo.`in`
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.setValue

class ChatRepositoryImpl(
    db: CoroutineDatabase
) : ChatRepository {

    private val chats = db.getCollection<Chat>()
    private val messages = db.getCollection<Message>()
    private val members = db.getCollection<Member>()

    override suspend fun getChatsForMember(member: String): List<ChatDto> {
        return chats.find(Chat::members contains member)
            .descendingSort(Chat::timestamp)
            .toList()
            .map { chat ->
                val lastMessage = chat.lastMessageId?.let { messages.findOneById(it) }
                val remoteUsername = chat.members.find { it != member }!!
                val remoteMember = members.findOneById(remoteUsername)!!
                ChatDto(
                    chatId = chat.id,
                    name = chat.name ?: remoteMember.username,
                    description = chat.description,
                    timestamp = chat.timestamp,
                    chatIconUrl = chat.iconUrl ?: remoteMember.profilePictureUrl,
                    lastMessage = lastMessage?.text,
                    isGroup = chat.members.size > 2
                )
            }
    }

    override suspend fun getChatById(chatId: String): Chat? {
        return chats.findOneById(chatId)
    }

    override suspend fun insertChat(
        members: List<String>,
        name: String?,
        lastMessageId: String?,
        chatIconUrl: String?,
        description: String?,
        id: String
    ): InsertOneResult = chats.insertOne(
        Chat(
            members = members,
            lastMessageId = lastMessageId,
            timestamp = System.currentTimeMillis(),
            name = name,
            iconUrl = chatIconUrl,
            description = description,
            id = id
        )
    )

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun updateLastMessageForChat(chatId: String, lastMessageId: String) {
        chats.updateOneById(chatId, setValue(Chat::lastMessageId, lastMessageId))
    }

    override suspend fun doesChatByMembersExist(members: List<String>): Boolean {
        return chats.find(Chat::members `in` members).first() != null
    }

    override suspend fun getMembersOfChat(chatId: String): List<Member> {
        val chat = chats.findOneById(chatId)
        return members.find(Member::username `in` chat!!.members).toList()
    }
}