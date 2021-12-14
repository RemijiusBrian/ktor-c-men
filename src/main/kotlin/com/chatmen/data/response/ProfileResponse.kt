package com.chatmen.data.response

data class ProfileResponse(
    val username: String,
    val isOwnProfile: Boolean,
    val profilePictureUrl: String? = null,
    val bio: String? = null
)
