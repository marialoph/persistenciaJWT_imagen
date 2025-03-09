package com.example

import com.example.com.domain.security.JwtConfig
import com.example.com.domain.usecase.ProviderUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("jwt-auth") {
            JwtConfig.configureAuthentication(this)
        }
    }

    routing {
        authenticate("jwt-auth") {
            get("/protected") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.getClaim("nombre", String::class)
                call.respondText("Hello, $username! You are authenticated.")
            }
        }
    }
}

// Método para validar el token
suspend fun ApplicationCall.validateToken(token: String): Boolean {
    val dataUser = this.principal<JWTPrincipal>()
    val nombre = dataUser?.payload?.getClaim("nombre")?.asString()
    val user = ProviderUseCase.getUsuarioByNombre(nombre!!)
    if (user == null || token != user.token) {
        this.respond(HttpStatusCode.Unauthorized, "Token inválido o usuario no disponible")
        return false
    }
    return true
}