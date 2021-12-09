package com.chatmen.data.websocket

import com.chatmen.data.model.Message

data class WsClientMessage(
    val toUsername: String,
    val text: String,
    val chatId: String?
) {
    fun toMessage(fromUsername: String, chat: String): Message = Message(
        fromUsername = fromUsername,
        chatId = chat,
        text = text,
        timestamp = System.currentTimeMillis(),
    )
}