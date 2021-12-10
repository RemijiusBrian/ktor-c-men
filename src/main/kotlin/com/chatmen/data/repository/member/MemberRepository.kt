package com.chatmen.data.repository.member

import com.chatmen.data.model.Member

interface MemberRepository {

    suspend fun getMemberByUsername(username: String): Member?

    suspend fun getAllMembers(): List<Member>

    suspend fun insertMember(member: Member)
}