package com.chatmen.data.repository.chat

import com.chatmen.data.model.Chat
import com.chatmen.data.model.Message
import com.chatmen.data.model.User
import com.chatmen.data.response.ChatDto
import com.mongodb.client.result.InsertOneResult
import org.litote.kmongo.`in`
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class ChatRepositoryImpl(
    db: CoroutineDatabase
) : ChatRepository {

    private val chats = db.getCollection<Chat>()
    private val messages = db.getCollection<Message>()
    private val users = db.getCollection<User>()

    override suspend fun getChatsForUser(username: String): List<ChatDto> {
        return chats.find(Chat::members contains username)
            .descendingSort(Chat::timestamp)
            .toList()
            .map { chat ->
                val lastMessage = chat.lastMessageId?.let { messages.findOneById(it) }
                ChatDto(
                    chatId = chat.id,
                    name = chat.name,
                    chatIconUrl = chat.iconUrl,
                    lastMessage = lastMessage?.text,
                    lastMessageTimestamp = lastMessage?.timestamp
                )
            }
    }

    override suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message> {
        return messages.find(Message::chatId eq chatId)
            .skip(page * pageSize)
            .limit(pageSize)
            .ascendingSort(Message::timestamp)
            .toList()
    }

    override suspend fun getChatById(chatId: String): Chat? {
        return chats.findOneById(chatId)
    }

    override suspend fun insertChat(
        members: List<String>,
        name: String?,
        lastMessageId: String?,
        chatIconUrl: String?
    ): InsertOneResult {
        val chat = Chat(
            members = members,
            lastMessageId = lastMessageId,
            timestamp = System.currentTimeMillis(),
            name = name,
            iconUrl = chatIconUrl
        )
        return chats.insertOne(chat)
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun updateLastMessageForChat(chatId: String, lastMessageId: String) {
        chats.updateOneById(chatId, setValue(Chat::lastMessageId, lastMessageId))
    }

    override suspend fun doesChatByUsersExist(users: List<String>): Boolean {
        return chats.find(Chat::members `in` users).first() != null
    }

    override suspend fun getMembersOfChat(chatId: String): List<User> {
        val chat = chats.findOneById(chatId)
        return users.find(
            User::username `in` chat!!.members
        ).toList()
    }
}