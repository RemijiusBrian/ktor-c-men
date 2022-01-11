package com.chatmen.service

import com.chatmen.data.model.Member
import com.chatmen.data.repository.member.MemberRepository
import com.chatmen.data.request.CreateAccountRequest
import com.chatmen.data.request.UpdateProfileRequest
import com.chatmen.data.response.ProfileResponse
import com.chatmen.util.Constants

class MemberService(
    private val repository: MemberRepository,
) {

    suspend fun getAllMembers(): List<Member> {
        return repository.getAllMembers()
    }

    suspend fun getMemberByUsername(username: String): Member? {
        return repository.getMemberByUsername(username)
    }

    suspend fun createMember(
        request: CreateAccountRequest
    ) {
        repository.insertMember(
            Member(
                username = request.username,
                password = request.password,
                profilePictureUrl = Constants.DEFAULT_PROFILE_PICTURE_PATH
            )
        )
    }

    suspend fun getMemberProfile(username: String, callingUsername: String): ProfileResponse? {
        val member = repository.getMemberByUsername(username) ?: return null
        return ProfileResponse(
            username = member.username,
            isOwnProfile = member.username == callingUsername,
            profilePictureUrl = member.profilePictureUrl,
            bio = member.bio
        )
    }

    suspend fun updateMemberProfile(
        username: String,
        profileImageUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean = repository.updateMemberProfile(username, profileImageUrl, updateProfileRequest)

    suspend fun doesMemberExist(username: String): Boolean {
        return repository.getMemberByUsername(username) != null
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