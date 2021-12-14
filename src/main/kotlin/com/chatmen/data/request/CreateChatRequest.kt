package com.chatmen.data.request

data class CreateChatRequest(
    val name: String?,
    val members: List<String>,
    val description: String?,
    val chatIconUrl: String?
)
