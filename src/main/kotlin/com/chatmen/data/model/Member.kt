package com.chatmen.data.model

import org.bson.codecs.pojo.annotations.BsonId

data class Member(
    @BsonId
    val username: String,
    val password: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
)