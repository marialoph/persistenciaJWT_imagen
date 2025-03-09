package com.example

import com.example.routing.authRouting
import com.example.routing.musicaRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {

        authRouting()
        musicaRouting()
        imgRouting()

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")


        staticFiles("/images", File("upload/images"))
        staticFiles("/files", File("upload/files"))

    }
}
