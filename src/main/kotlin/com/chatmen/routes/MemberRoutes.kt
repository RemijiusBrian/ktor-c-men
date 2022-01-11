package com.chatmen.routes

import com.chatmen.data.request.UpdateProfileRequest
import com.chatmen.data.response.BasicApiResponse
import com.chatmen.plugins.username
import com.chatmen.service.MemberService
import com.chatmen.util.ApiResponseMessages
import com.chatmen.util.Constants.BASE_URL
import com.chatmen.util.Constants.PROFILE_PICTURE_PATH
import com.chatmen.util.QueryParams
import com.chatmen.util.save
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.getAllMembers(memberService: MemberService) {
    authenticate {
        get("/api/members") {
            call.respond(memberService.getAllMembers())
        }
    }
}

fun Route.getMemberProfile(memberService: MemberService) {
    authenticate {
        get("/api/member/profile") {
            val username = call.parameters[QueryParams.USERNAME]
            if (username.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val profileResponse = memberService.getMemberProfile(
                username, call.username!!
            )

            if (profileResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        data = profileResponse
                    )
                )
            }
        }
    }
}

fun Route.updateMemberProfile(memberService: MemberService) {
    authenticate {
        val gson: Gson by inject()

        put("/api/member/update") {
            val multipart = call.receiveMultipart()

            var updateProfileRequest: UpdateProfileRequest? = null
            var profilePictureFileName: String? = null

            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        if (partData.name == "profile_picture") {
                            profilePictureFileName = partData.save(PROFILE_PICTURE_PATH)
                        }
                    }
                    is PartData.BinaryItem -> Unit
                }
            }
            val profilePictureUrl = "${BASE_URL}profile_pictures/$profilePictureFileName"

            updateProfileRequest?.let { updateRequest ->
                val updateAcknowledged = memberService.updateMemberProfile(
                    username = call.username!!,
                    profileImageUrl = if (profilePictureFileName == null) null
                    else profilePictureUrl,
                    updateProfileRequest = updateRequest
                )

                if (updateAcknowledged) {
                    call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
                } else {
                    File("${PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}