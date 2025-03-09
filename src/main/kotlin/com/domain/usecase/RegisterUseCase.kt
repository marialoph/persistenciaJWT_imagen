package com.example.com.domain.usecase

import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import com.example.com.domain.repository.UsuarioInterface


class RegisterUseCase(val repository: UsuarioInterface) {
    suspend operator fun invoke(usuario: UpdateUsuario): Usuario? {
        usuario.nombre = usuario.nombre ?: throw IllegalArgumentException("El nombre no puede ser nulo.")
        usuario.contrasena = usuario.contrasena ?: throw IllegalArgumentException("La contraseña no puede ser nula.")
        usuario.token = null
        val isRegistered = repository.registrarUsuario(usuario)
        return if (isRegistered) {
            Usuario(
                id = null,
                nombre = usuario.nombre!!,
                contrasena = usuario.contrasena!!,
                token = usuario.token
            )
        } else {
            null
        }
    }
}


