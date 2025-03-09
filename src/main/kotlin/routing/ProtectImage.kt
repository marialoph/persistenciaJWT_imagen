package com.example

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.imgRouting(){
    route("/images/{nombre}/{image}") {
        authenticate("jwt-auth") {

            get() {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val validate = call.validateToken(token!!)
                if (!validate)
                    return@get

                val nombre = call.parameters["nombre"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Necesitamos un nombre")
                val nameImage = call.parameters["image"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Necesitamos la imagen"
                )
                val path = ApplicationContext.context.environment.config.property("ktor.path.images").getString() + "/$nombre"
                val img = File(path, nameImage)
                if (!img.exists()){
                    return@get call.respond(HttpStatusCode.BadRequest, "Imagen no encontrada")
                }

                call.respondFile(img)
            }
        }
    }
}