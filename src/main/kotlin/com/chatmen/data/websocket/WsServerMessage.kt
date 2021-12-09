package com.chatmen.data.websocket

import com.chatmen.data.model.Message

data class WsServerMessage(
    val fromUsername: String,
    val toUsername: String,
    val text: String,
    val timestamp: Long,
    val chatId: String,
) {
    fun toMessage(): Message = Message(
        fromUsername = fromUsername,
        text = text,
        timestamp = timestamp,
        chatId = chatId
    )

}