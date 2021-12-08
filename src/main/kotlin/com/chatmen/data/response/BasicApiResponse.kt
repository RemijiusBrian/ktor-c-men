package com.chatmen.data.response

import kotlinx.serialization.Serializable

@Serializable
data class BasicApiResponse<T>(
    val successful: Boolean,
    val message: String? = null,
    val data: T? = null
)