package com.chatmen.data.websocket

data class WsServerMessage(
    val messageId: String,
    val fromUsername: String,
    val text: String,
    val timestamp: Long,
    val chatId: String,
)