package com.chatmen.routes

import com.chatmen.data.websocket.WsClientMessage
import com.chatmen.service.chat.ChatController
import com.chatmen.service.chat.ChatService
import com.chatmen.util.Constants
import com.chatmen.util.QueryParams
import com.chatmen.util.WebSocketObject
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.getChatsForUser(chatService: ChatService) {
    authenticate {
        get("/api/chats") {
            val username = call.parameters[QueryParams.USERNAME] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val chats = chatService.getChatsForUser(username)
            call.respond(HttpStatusCode.OK, chats)
        }
    }
}

fun Route.getMessagesForChat(chatService: ChatService) {
    authenticate {
        get("/api/chat/messages") {
            val chatId = call.parameters[QueryParams.CHAT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val page = call.parameters[QueryParams.PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

            val messages = chatService.getMessagesForChat(chatId, page, pageSize)
            call.respond(HttpStatusCode.OK, messages)
        }
    }
}

fun Route.chatWebSocket(chatController: ChatController) {
    authenticate {
        webSocket("/api/chat/websocket") {
            val username = call.parameters[QueryParams.USERNAME] ?: kotlin.run {
                call.respond(HttpStatusCode.OK)
                return@webSocket
            }
            chatController.onJoin(username, this)
            try {
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val frameText = frame.readText()
                            val delimiterIndex = frameText.indexOf("#")
                            if (delimiterIndex == -1) {
                                println("No delimiter found")
                                return@webSocket
                            }
                            val type = frameText.substring(0, delimiterIndex).toIntOrNull()
                            if (type == null) {
                                println("Invalid format")
                                return@webSocket
                            }
                            val json = frameText.substring(delimiterIndex + 1, frameText.length)
                            handleWebSocket(username, Gson(), chatController, type, frameText, json)
                        }
                        else -> Unit
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                println("Disconnecting $username")
                chatController.onDisconnect(username)
            }
        }
    }
}

suspend fun handleWebSocket(
    username: String,
    gson: Gson,
    chatController: ChatController,
    type: Int,
    frameText: String,
    json: String
) {
    when (type) {
        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJson(json, WsClientMessage::class.java)
            chatController.sendMessage(username, message)
        }
    }
}