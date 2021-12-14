package com.chatmen.service.chat

import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.repository.member.MemberRepository
import com.chatmen.data.request.CreateChatRequest
import com.chatmen.data.response.ChatDto

class ChatService(
    private val repository: ChatRepository,
    private val memberRepository: MemberRepository
) {

    suspend fun getChatsForMember(member: String): List<ChatDto> {
        return repository.getChatsForMember(member)
    }

    suspend fun createChat(createChatRequest: CreateChatRequest, callerUsername: String): ChatDto? {
        val allMembers = createChatRequest.members + callerUsername
        val insertResult = repository.insertChat(
            members = allMembers,
            name = createChatRequest.name,
            chatIconUrl = createChatRequest.chatIconUrl,
            description = createChatRequest.description
        )
        if (insertResult.wasAcknowledged()) {
            val createdChat = repository.getChatById(insertResult.insertedId!!.toString())!!
            val remoteUsername = createdChat.members.find { it != callerUsername }!!
            val remoteMember = memberRepository.getMemberByUsername(remoteUsername)!!

            return ChatDto(
                chatId = createdChat.id,
                name = createdChat.name ?: remoteMember.username,
                description = createdChat.description,
                timestamp = createdChat.timestamp,
                chatIconUrl = createdChat.iconUrl ?: remoteMember.profilePictureUrl,
                isGroup = createdChat.members.size > 2,
                lastMessage = null
            )
        } else {
            return null
        }
    }
}