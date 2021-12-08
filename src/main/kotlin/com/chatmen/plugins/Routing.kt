package com.chatmen.plugins

import com.chatmen.routes.*
import com.chatmen.service.UserService
import com.chatmen.service.chat.ChatController
import com.chatmen.service.chat.ChatService
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userService: UserService by inject() // User Service
    val chatService: ChatService by inject() // Chat Service
    val chatController: ChatController by inject() // Chat Controller

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    install(Routing) {
        // Auth Routes
        authenticate()
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Chat Service
        getChatsForUser(chatService)
        getMessagesForChat(chatService)
        chatWebSocket(chatController)
    }
}
