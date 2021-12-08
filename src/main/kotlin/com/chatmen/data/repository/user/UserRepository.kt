package com.chatmen.data.repository.user

import com.chatmen.data.model.User

interface UserRepository {

    suspend fun getUserByUsername(username: String): User?

    suspend fun getAllUsers(): List<User>
}