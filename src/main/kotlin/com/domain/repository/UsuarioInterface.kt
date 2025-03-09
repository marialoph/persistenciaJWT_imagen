package com.example.com.domain.repository

import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario

interface UsuarioInterface {
    suspend fun registrarUsuario(usuario: UpdateUsuario): Boolean
    suspend fun login(nombre: String, contrasena: String): Usuario?
    suspend fun updateUsuario(usuario: Usuario): Boolean

}
