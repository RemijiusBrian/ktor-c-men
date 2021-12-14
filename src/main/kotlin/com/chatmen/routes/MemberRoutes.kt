package com.chatmen.routes

import com.chatmen.service.MemberService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAllMembers(memberService: MemberService) {
    authenticate {
        get("/api/members") {
            call.respond(memberService.getAllMembers())
        }
    }
}