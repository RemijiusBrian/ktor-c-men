package com.chatmen.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.configureHTTP() {
    install(HttpsRedirect) {
            // The port to redirect to. By default, 443, the default HTTPS port.
            sslPort = 443
            // 301 Moved Permanently, or 302 Found redirect.
            permanentRedirect = true
        }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
        header("Content-Type", "application/json")
    }
}
