package com.chatmen.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Chat(
    val members: List<String>,
    val lastMessageId: String? = null,
    val timestamp: Long,
    val name: String? = null,
    val iconUrl: String? = null,
    @BsonId
    val id: String = ObjectId().toString()
)
