package com.chatmen.data.repository.member

import com.chatmen.data.model.Member
import com.chatmen.data.request.UpdateProfileRequest

interface MemberRepository {

    suspend fun getMemberByUsername(username: String): Member?

    suspend fun getAllMembers(): List<Member>

    suspend fun insertMember(member: Member)

    suspend fun updateMemberProfile(
        username: String,
        profileImageUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean
}