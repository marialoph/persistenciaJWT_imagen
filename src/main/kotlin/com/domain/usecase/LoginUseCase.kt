package com.example.com.domain.usecase
import com.example.com.domain.models.Usuario
import com.example.com.domain.repository.UsuarioInterface
import com.example.com.domain.security.JwtConfig

class LoginUseCase(val repository: UsuarioInterface) {
    suspend operator fun invoke(nombre: String?, pass: String?): Usuario? {
        if (nombre.isNullOrBlank() || pass.isNullOrBlank()) return null
        return try {
            val usuario = repository.login(nombre, pass) ?: return null
            val token = JwtConfig.generateToken(usuario.nombre)
            val updateUsuario = usuario.copy(token = token)
            val updateResult = repository.updateUsuario(updateUsuario)
            if (updateResult) {
                updateUsuario
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error en el login: ${e.localizedMessage}")
            null
        }
    }
}
