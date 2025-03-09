package com.example.routing

import com.example.com.data.persistence.models.UsuarioDao
import com.example.com.data.persistence.models.UsuarioTable
import com.example.com.data.secutiry.PasswordHash
import com.example.com.domain.mapper.toUpdateUsuario
import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import com.example.com.domain.security.JwtConfig
import com.example.com.domain.usecase.ProviderUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.authRouting() {

    post("/auth") {
        val loginUsuario = call.receive<Usuario>()
        val usuario = transaction {
            UsuarioDao.find { UsuarioTable.nombre eq loginUsuario.nombre }.singleOrNull()
        }
        if (usuario == null || usuario.contrasena != loginUsuario.contrasena) {
            call.respond(HttpStatusCode.Unauthorized, "Usuario o contraseña incorrectos")
            return@post
        }
        val token = if (usuario.token.isNullOrEmpty()) {
            val generatedToken = JwtConfig.generateToken(usuario.nombre)
            transaction {
                usuario.token = generatedToken
            }
            generatedToken
        } else {
            usuario.token!!
        }
        val response = hashMapOf(
            "token" to token,
            "nombre" to usuario.nombre,
            "contrasena" to usuario.contrasena
        )

        call.respond(HttpStatusCode.OK, response)
    }



    route("/register") {
        post {
            try {
                val newUser = call.receive<UpdateUsuario>()
                val registeredUser = ProviderUseCase.register(newUser)

                if (registeredUser != null) {
                    val responseUser = registeredUser.toUpdateUsuario()
                    call.respond(HttpStatusCode.Created, responseUser)
                } else {
                    call.respond(HttpStatusCode.Conflict, "No se ha podido realizar el registro")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Error en el formato de envío de datos o lectura del cuerpo.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Problemas en la conversión JSON o en el proceso.")
            }
        }
    }


}