package com.chatmen.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.chatmen.data.request.LoginRequest
import com.chatmen.data.response.AuthResponse
import com.chatmen.data.response.BasicApiResponse
import com.chatmen.service.UserService
import com.chatmen.util.ApiResponseMessages.INVALID_CREDENTIALS
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {
    post("api/user/login") {
        val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (request.username.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userService.getUserByUsername(request.username) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
            return@post
        }

        val isPasswordCorrect = userService.isPasswordValid(
            enteredPassword = request.password,
            actualPassword = user.password
        )

        if (isPasswordCorrect) {
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("username", user.username)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = AuthResponse(
                        username = user.username,
                        token = token
                    )
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
        }
    }
}

fun Route.authenticate() {
    authenticate {
        get("/api/user/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}