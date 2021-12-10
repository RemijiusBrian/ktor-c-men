package com.chatmen.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.chatmen.data.request.CreateAccountRequest
import com.chatmen.data.request.LoginRequest
import com.chatmen.data.response.AuthResponse
import com.chatmen.data.response.BasicApiResponse
import com.chatmen.service.MemberService
import com.chatmen.util.ApiResponseMessages.FIELDS_BLANK
import com.chatmen.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.chatmen.util.ApiResponseMessages.USERNAME_ALREADY_EXISTS
import com.chatmen.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.createMemberAccount(
    memberService: MemberService,
) {
    post("/api/member/create") {
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (memberService.doesMemberExist(request.username)) {
            call.respond(
                BasicApiResponse<Unit>(
                    successful = false,
                    message = USERNAME_ALREADY_EXISTS
                )
            )
        }

        when (memberService.validateCreationRequest(request)) {
            MemberService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
            }
            MemberService.ValidationEvent.Success -> {
                memberService.createMember(request)
                call.respond(BasicApiResponse<Unit>(successful = true))
            }
        }
    }
}

fun Route.loginMember(
    memberService: MemberService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {
    post("/api/member/login") {
        val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (request.username.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = memberService.getMemberByUsername(request.username) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
            return@post
        }

        val isPasswordCorrect = memberService.isPasswordValid(
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
        get("/api/member/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}