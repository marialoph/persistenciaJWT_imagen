package com.example

import configureContext
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureContext(this)
    configureSerialization()
    configureSecurity()
    configureDatabases()
    configureRouting()
}
