package com.chatmen.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val chatId: String,
    val name: String?,
    val chatIconUrl: String?,
    val lastMessage: String?,
    val lastMessageTimestamp: Long?
)
