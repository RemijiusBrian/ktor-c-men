package com.chatmen.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    val fromUsername: String,
    val text: String,
    val timestamp: Long,
    val chatId: String,
    @BsonId
    val id: String = ObjectId().toString()
)
