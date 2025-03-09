package com.example.com.domain.mapper

import com.example.com.data.persistence.models.MusicaDao
import com.example.com.data.persistence.models.UsuarioTable
import com.example.com.data.persistence.models.UsuarioTable.token
import com.example.com.data.secutiry.PasswordHash
import com.example.com.domain.models.Musica
import com.example.com.domain.models.UpdateMusica
import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


fun MusicaDao.toMusica(): Musica {
    return Musica(
        nombre = this.nombre,
        generoMusical = this.generoMusical,
        albums = this.albums,
        fechaNacimiento = this.fechaNacimiento,
        image = this.image,

        )
}

fun Musica.toUpdateMusica(): UpdateMusica {
    return UpdateMusica(
        nombre = this.nombre,
        generoMusical = this.generoMusical,
        albums = this.albums,
        fechaNacimiento = this.fechaNacimiento,
        image = this.image,

        )
}

fun UpdateMusica.toMusica(): Musica {
    return Musica(
        nombre = this.nombre,
        generoMusical = this.generoMusical ?: "",
        albums = this.albums ?: "",
        fechaNacimiento = this.fechaNacimiento ?: "",
        image = this.image ?: "",

        )
}
fun updateToken(usuarioId: Int, newToken: String) {
    transaction {
        UsuarioTable.update({ UsuarioTable.id eq usuarioId }) {
            it[token] = newToken
        }
    }
}



fun insertUsuario(usuario: UpdateUsuario) {
    transaction {
        val nombre = usuario.nombre ?: throw IllegalArgumentException("El nombre no puede ser nulo.")
        val contrasena = usuario.contrasena ?: throw IllegalArgumentException("La contraseña no puede ser nula.")

        // Insertamos el nuevo usuario en la base de datos sin el token
        UsuarioTable.insert {
            it[UsuarioTable.nombre] = nombre
            it[UsuarioTable.contrasena] = PasswordHash.hash(contrasena)
            it[UsuarioTable.token] = null
        }
    }
}



fun Usuario.toUpdateUsuario(): UpdateUsuario {
    return UpdateUsuario(
        nombre = this.nombre,
        contrasena = this.contrasena,
        token = ""
    )
}

