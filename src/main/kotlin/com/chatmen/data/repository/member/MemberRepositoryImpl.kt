package com.chatmen.data.repository.member

import com.chatmen.data.model.Member
import com.chatmen.data.request.UpdateProfileRequest
import org.litote.kmongo.coroutine.CoroutineDatabase

class MemberRepositoryImpl(
    db: CoroutineDatabase
) : MemberRepository {

    private val members = db.getCollection<Member>()

    override suspend fun getMemberByUsername(username: String): Member? {
        return members.findOneById(username)
    }

    override suspend fun getAllMembers(): List<Member> {
        return members.find().toList()
    }

    override suspend fun insertMember(member: Member) {
        members.insertOne(member)
    }

    override suspend fun updateMemberProfile(
        username: String,
        profileImageUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        val member = members.findOneById(username) ?: return false

        return members.updateOneById(
            username,
            update = Member(
                username = updateProfileRequest.username,
                bio = updateProfileRequest.bio,
                password = member.password,
                profilePictureUrl = profileImageUrl
            )
        ).wasAcknowledged()
    }
}