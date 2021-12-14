package com.chatmen.data.response

data class ChatDto(
    val chatId: String,
    val name: String,
    val description: String?,
    val timestamp: Long,
    val chatIconUrl: String?,
    val lastMessage: String?,
    val isGroup: Boolean
)