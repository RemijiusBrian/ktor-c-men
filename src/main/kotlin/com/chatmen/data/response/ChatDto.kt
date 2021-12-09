package com.chatmen.data.response

data class ChatDto(
    val chatId: String,
    val name: String?,
    val chatIconUrl: String?,
    val lastMessage: String?,
    val lastMessageTimestamp: Long?
)
