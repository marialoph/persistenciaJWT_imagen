package com.example.routing

import com.example.com.domain.models.Musica
import com.example.com.domain.models.UpdateMusica
import com.example.com.domain.usecase.ProviderUseCase
import com.example.com.domain.usecase.ProviderUseCase.logger
import com.example.validateToken
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.musicaRouting() {
    route("/") {
        get {
            call.respondText("Hello World!")
        }
    }

    route("/musica") {
        authenticate("jwt-auth") {

            // Obtener toda la música
            get {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val validate = call.validateToken(token!!)

                if (!validate) {
                    call.respond(HttpStatusCode.Unauthorized, "Token inválido")
                    return@get
                }

                val musicaName = call.request.queryParameters["name"]
                logger.warn("El nombre de la música es $musicaName")

                if (musicaName != null) {
                    val musica = ProviderUseCase.getMusicaByName(musicaName)

                    if (musica.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "Música no encontrada")
                    } else {
                        call.respond(musica)
                    }
                } else {
                    val musicas = ProviderUseCase.getAllMusica()
                    call.respond(musicas)
                }
            }

            // Crear artista
            post {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val validate = call.validateToken(token!!)
                if (!validate) {
                    call.respond(HttpStatusCode.Unauthorized, "Token inválido")
                    return@post
                }
                try {
                    val musica = call.receive<Musica>()

                    val res = ProviderUseCase.insertMusica(musica)

                    if (!res) {
                        call.respond(HttpStatusCode.Conflict, "El artista no pudo insertarse. Puede que ya exista.")
                        return@post
                    }
                    call.respond(HttpStatusCode.Created, "Artista creado correctamente con nombre: ${musica.nombre}")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error en los datos proporcionados.")
                }
            }

            // Actualizar artista
            patch("{nombreMusica}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val validate = call.validateToken(token!!)
                if (!validate) {
                    call.respond(HttpStatusCode.Unauthorized, "Token inválido")
                    return@patch
                }
                try {
                    val nombreMusica = call.parameters["nombreMusica"]
                    if (nombreMusica == null) {
                        call.respond(HttpStatusCode.BadRequest, "Debes proporcionar el nombre del artista para actualizar.")
                        return@patch
                    }

                    val updateMusica = call.receive<UpdateMusica>()
                    val res = ProviderUseCase.updateMusica(updateMusica, nombreMusica)

                    if (!!res!!) {
                        call.respond(HttpStatusCode.Conflict, "El artista no pudo modificarse. Puede que no exista.")
                        return@patch
                    }

                    call.respond(HttpStatusCode.OK, "Artista con nombre: $nombreMusica ha sido actualizada.")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error al actualizar el artista.")
                }
            }

            // Eliminar artista
            delete("{nombreMusica}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val validate = call.validateToken(token!!)

                if (!validate) {
                    call.respond(HttpStatusCode.Unauthorized, "Token inválido")
                    return@delete
                }

                try {
                    val nombreMusica = call.parameters["nombreMusica"]
                    if (nombreMusica == null) {
                        call.respond(HttpStatusCode.BadRequest, "Debes proporcionar el nombre del artista para eliminar.")
                        return@delete
                    }

                    val res = ProviderUseCase.deleteMusica(nombreMusica)

                    if (!res) {
                        call.respond(HttpStatusCode.NotFound, "No se pudo eliminar el artista. Puede que no exista.")
                        return@delete
                    }

                    call.respond(HttpStatusCode.OK, "El artista con nombre: $nombreMusica ha sido eliminada.")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error al eliminar el artista.")
                }
            }
        }
    }
}
