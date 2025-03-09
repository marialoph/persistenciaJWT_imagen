package com.example.com.data.inmemory.repository

import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import com.example.com.domain.repository.UsuarioInterface

class MemoryUsuarioRepository :UsuarioInterface {
    override suspend fun registrarUsuario(usuario: UpdateUsuario): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun login(nombre: String, contrasena: String): Usuario? {
        TODO("Not yet implemented")
    }

    override suspend fun updateUsuario(usuario: Usuario): Boolean {
        TODO("Not yet implemented")
    }
}