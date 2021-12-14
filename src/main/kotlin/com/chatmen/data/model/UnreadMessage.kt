package com.chatmen.data.model

import org.bson.codecs.pojo.annotations.BsonId

data class UnreadMessage(
    @BsonId
    val messageId: String,
    val toMembers: List<String>
)