package com.chatmen.di

import com.chatmen.data.repository.chat.ChatRepository
import com.chatmen.data.repository.chat.ChatRepositoryImpl
import com.chatmen.data.repository.member.MemberRepository
import com.chatmen.data.repository.member.MemberRepositoryImpl
import com.chatmen.service.MemberService
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
    // Member Repository
    single<MemberRepository> {
        MemberRepositoryImpl(get())
    }
    // Chat Repository
    single<ChatRepository> {
        ChatRepositoryImpl(get())
    }

    // ---- Services ----
    // Member Service
    single { MemberService(get()) }
    // Chat Service
    single { ChatService(get()) }

    // Chat Controller
    single { ChatController(get()) }

    // Gson
    single { Gson() }
}