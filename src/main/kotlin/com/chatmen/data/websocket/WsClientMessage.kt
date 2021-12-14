package com.chatmen.data.websocket

import com.chatmen.data.model.Message

data class WsClientMessage(
    val text: String,
    val chatId: String?,
    val members: List<String> = emptyList()
) {
    fun toMessage(fromUsername: String, chat: String, id: String): Message = Message(
        fromUsername = fromUsername,
        chatId = chatId ?: chat,
        text = text,
        timestamp = System.currentTimeMillis(),
        id = id
    )
}