package com.chatmen.di

import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.repository.chat.ChatRepositoryImpl
import com.chatmen.data.repository.user.UserRepository
import com.chatmen.data.repository.user.UserRepositoryImpl
import com.chatmen.service.UserService
import com.chatmen.service.chat.ChatController
import com.chatmen.service.chat.ChatService
import com.chatmen.util.Constants.DATABASE_NAME
import com.google.gson.Gson
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val MainModule = module {
    // KMongo DB
    single {
        KMongo
            .createClient()
            .coroutine
            .getDatabase(DATABASE_NAME)
    }

    // ---- Repositories ----
    // User Repository
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    // Chat Repository
    single<ChatRepository> {
        ChatRepositoryImpl(get())
    }

    // ---- Services ----
    // User Service
    single { UserService(get()) }
    // Chat Service
    single { ChatService(get()) }

    // Chat Controller
    single { ChatController(get()) }

    // Gson
    single { Gson() }
}