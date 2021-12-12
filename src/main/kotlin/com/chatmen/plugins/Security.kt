package com.chatmen.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity() {

    authentication {
        jwt {
            val jwtAudience = environment.config.property("jwt.audience").getString()
            realm = environment.config.property("jwt.realm").getString()
            val jwtSecret = environment.config.property("jwt.secret").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(environment.config.property("jwt.domain").getString())
                    .build()
            )
            validate { credential ->
                print(credential.payload.audience)
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

val JWTPrincipal.username: String?
    get() = getClaim("username", String::class)