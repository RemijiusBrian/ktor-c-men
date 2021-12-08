package com.chatmen.service.chat

import com.chatmen.data.model.Message
import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.response.ChatDto

class ChatService(
    private val chatRepository: ChatRepository,
) {

    suspend fun getChatsForUser(username: String): List<ChatDto> {
        return chatRepository.getChatsForUser(username)
    }

    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message> {
        return chatRepository.getMessagesForChat(chatId, page, pageSize)
    }
}