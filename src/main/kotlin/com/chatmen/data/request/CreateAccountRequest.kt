package com.chatmen.data.request

data class CreateAccountRequest(
    val username: String,
    val password: String,
    val name: String = username,
    val email: String? = null,
)