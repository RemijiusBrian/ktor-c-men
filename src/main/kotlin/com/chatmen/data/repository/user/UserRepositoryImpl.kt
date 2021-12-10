package com.chatmen.data.repository.user

import com.chatmen.data.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {

    private val users = db.getCollection<User>()

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOneById(username)
    }

    override suspend fun getAllUsers(): List<User> {
        return users.find().toList()
    }

    override suspend fun insertUser(user: User) {
        users.insertOne(user)
    }
}