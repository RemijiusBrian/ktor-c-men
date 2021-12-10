package com.chatmen.service

import com.chatmen.data.model.User
import com.chatmen.data.repository.user.UserRepository
import com.chatmen.data.request.CreateAccountRequest
import com.chatmen.data.response.ProfileResponse
import com.chatmen.util.Constants

class UserService(
    private val repository: UserRepository
) {
    suspend fun getUserByUsername(username: String): User? {
        return repository.getUserByUsername(username)
    }

    suspend fun createUser(
        request: CreateAccountRequest
    ) {
        repository.insertUser(
            User(
                username = request.username,
                password = request.password,
                name = request.name,
                email = request.email,
                profilePictureUrl = Constants.DEFAULT_PROFILE_PICTURE_PATH
            )
        )
    }

    suspend fun getUserProfile(username: String, calledUsername: String): ProfileResponse? {
        val user = repository.getUserByUsername(username) ?: return null
        return ProfileResponse(
            username = user.username,
            name = user.name,
            isOwnProfile = user.username == calledUsername,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
            bio = user.bio
        )
    }

    suspend fun doesUsernameExist(username: String): Boolean {
        return repository.getUserByUsername(username) != null
    }

    fun isPasswordValid(enteredPassword: String, actualPassword: String): Boolean =
        enteredPassword == actualPassword

    fun validateCreationRequest(request: CreateAccountRequest): ValidationEvent {
        if (request.username.isBlank() || request.password.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }

        return ValidationEvent.Success
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}