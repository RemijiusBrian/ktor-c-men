package com.chatmen

import com.chatmen.di.MainModule
import com.chatmen.plugins.*
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(MainModule)
    }
    configureSecurity()
    configureHTTP()
    configureSockets()
    configureRouting()
    configureSerialization()
    configureMonitoring()
}