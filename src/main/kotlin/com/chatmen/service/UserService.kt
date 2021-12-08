package com.chatmen.service

import com.chatmen.data.model.User
import com.chatmen.data.repository.user.UserRepository

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun getUserByUsername(username: String): User? {
        return userRepository.getUserByUsername(username)
    }

    fun isPasswordValid(enteredPassword: String, actualPassword: String): Boolean =
        enteredPassword == actualPassword
}