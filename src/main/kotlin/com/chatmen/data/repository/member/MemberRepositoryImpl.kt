package com.chatmen.data.repository.member

import com.chatmen.data.model.Member
import org.litote.kmongo.coroutine.CoroutineDatabase

class MemberRepositoryImpl(
    db: CoroutineDatabase
) : MemberRepository {

    private val users = db.getCollection<Member>()

    override suspend fun getMemberByUsername(username: String): Member? {
        return users.findOneById(username)
    }

    override suspend fun getAllMembers(): List<Member> {
        return users.find().toList()
    }

    override suspend fun insertMember(member: Member) {
        users.insertOne(member)
    }
}