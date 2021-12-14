package com.chatmen.routes

import com.chatmen.data.request.CreateChatRequest
import com.chatmen.data.response.BasicApiResponse
import com.chatmen.plugins.username
import com.chatmen.service.chat.ChatController
import com.chatmen.service.chat.ChatCreationException
import com.chatmen.service.chat.ChatService
import com.chatmen.service.chat.InsufficientMembersException
import com.chatmen.util.ApiResponseMessages.ERROR_CREATING_CHAT
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.getChatsForUser(chatService: ChatService) {
    authenticate {
        get("/api/chats") {
            val chats = chatService.getChatsForMember(call.username!!)
            call.respond(HttpStatusCode.OK, chats)
        }
    }
}

fun Route.createChat(chatService: ChatService) {
    authenticate {
        post("/api/chat/create") {
            val createChatRequest = call.receiveOrNull<CreateChatRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            chatService.createChat(createChatRequest, call.username!!)?.let { chatDto ->
                call.respond(
                    BasicApiResponse(
                        successful = true,
                        data = chatDto
                    )
                )
            } ?: call.respond(
                BasicApiResponse<Unit>(
                    successful = false,
                    message = ERROR_CREATING_CHAT
                )
            )
        }
    }
}

fun Route.chatWebSocket(chatController: ChatController) {
    authenticate {
        webSocket("/api/chat/web-socket") {
            val username = call.username!!
            chatController.onJoin(username, this)
            chatController.sendUnreadMessagesIfAny(username)
            try {
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val frameText = frame.readText()
                            /*val delimiterIndex = frameText.indexOf("#")
                            if (delimiterIndex == -1) {
                                println("No delimiter found")
                                return@webSocket
                            }
                            val type = frameText.substring(0, delimiterIndex).toIntOrNull()
                            if (type == null) {
                                println("Invalid format")
                                return@webSocket
                            }*/

                            // val json = frameText.substring(delimiterIndex + 1, frameText.length)
                            handleWebSocket(username, chatController, frameText)
                        }
                        else -> Unit
                    }
                }
            } catch (e: InsufficientMembersException) {
                call.respond(HttpStatusCode.InternalServerError)
            } catch (e: ChatCreationException) {
                call.respond(HttpStatusCode.InternalServerError)
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
    chatController: ChatController,
    frameText: String,
) {
    chatController.sendMessage(username, frameText)
    /*when (type) {
        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJson(json, WsClientMessage::class.java)
            chatController.sendMessage(username, message, gson)
        }
    }*/
}