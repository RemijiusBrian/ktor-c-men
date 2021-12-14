package com.chatmen.data.model

import com.chatmen.data.websocket.WsServerMessage
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Message(
    val fromUsername: String,
    val text: String,
    val timestamp: Long,
    val chatId: String,
    @BsonId
    val id: String = ObjectId().toString()
) {
    fun toWsServerMessage(): WsServerMessage = WsServerMessage(
        messageId = id,
        fromUsername = fromUsername,
        text = text,
        timestamp = timestamp,
        chatId = chatId
    )
}