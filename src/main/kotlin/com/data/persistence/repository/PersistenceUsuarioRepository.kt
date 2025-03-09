package com.example.com.data.persistence.repository

import com.example.com.data.persistence.models.UsuarioTable

import com.example.com.data.secutiry.PasswordHash
import com.example.com.domain.mapper.insertUsuario
import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import com.example.com.domain.repository.UsuarioInterface
import com.example.com.domain.security.JwtConfig
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PersistenceUsuarioRepository : UsuarioInterface {

    override suspend fun login(nombre: String, pass: String): Usuario? {
        val usuario = getUsuarioByNombre(nombre) ?: return null

        return try {
            val posibleHash = PasswordHash.hash(pass)
            if (posibleHash == usuario.contrasena) {
                val newToken = JwtConfig.generateToken(usuario.nombre)

                transaction {
                    val usuarioId = usuario.id ?: throw IllegalArgumentException("El usuario no tiene un ID válido")

                    UsuarioTable.update({ UsuarioTable.id eq usuarioId }) {
                        it[UsuarioTable.token] = newToken
                    }
                }
                usuario.copy(token = newToken)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error en la autenticación: ${e.localizedMessage}")
            null
        }
    }


    override suspend fun registrarUsuario(usuario: UpdateUsuario): Boolean {
        return try {
            insertUsuario(usuario)
            true
        } catch (e: Exception) {
            println("Error en el registro de usuario: ${e.localizedMessage}")
            false
        }
    }


    private fun getUsuarioByNombre(nombre: String): Usuario? {
        return try {
            transaction {
                UsuarioTable
                    .select { UsuarioTable.nombre eq nombre }
                    .map {
                        Usuario(
                            id = it[UsuarioTable.id].value,
                            nombre = it[UsuarioTable.nombre],
                            contrasena = it[UsuarioTable.contrasena],
                            token = it[UsuarioTable.token]
                        )
                    }
                    .singleOrNull()
            }
        } catch (e: Exception) {
            println("Error al obtener el usuario: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun updateUsuario(usuario: Usuario): Boolean {
        return try {
            transaction {
                UsuarioTable.update({ UsuarioTable.id eq usuario.id!! }) {
                    it[UsuarioTable.token] = usuario.token ?: ""
                }
            }
            true
        } catch (e: Exception) {
            println("Error al actualizar el token del usuario: ${e.localizedMessage}")
            false
        }
    }
}