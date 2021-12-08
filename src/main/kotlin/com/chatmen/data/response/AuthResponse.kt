package com.chatmen.data.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val username: String,
    val token: String
)