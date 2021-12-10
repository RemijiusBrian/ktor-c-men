package com.chatmen.data.response

data class ProfileResponse(
    val username: String,
    val name: String,
    val isOwnProfile: Boolean,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val bio: String? = null
)
