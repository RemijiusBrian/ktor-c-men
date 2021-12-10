package com.chatmen.routes

import com.chatmen.service.MemberService
import com.chatmen.util.QueryParams
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getProfile(memberService: MemberService) {
    authenticate {
        get("/api/user/profile") {
            val username = call.parameters[QueryParams.USERNAME]
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val profileResponse = memberService.getMemberProfile(username, username)
        }
    }
}