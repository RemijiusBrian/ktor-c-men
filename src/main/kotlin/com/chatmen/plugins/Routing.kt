package com.chatmen.plugins

import com.chatmen.routes.authenticate
import com.chatmen.routes.createUser
import com.chatmen.routes.loginUser
import com.chatmen.service.UserService
import com.chatmen.service.chat.ChatController
import com.chatmen.service.chat.ChatService
import io.ktor.application.*
import io.ktor.http.content.*
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
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Chat Service
//        getChatsForUser(chatService)
//        getMessagesForChat(chatService)
//        chatWebSocket(chatController)

        // Static Resources
        static {
            resources("static")
        }
    }
}
