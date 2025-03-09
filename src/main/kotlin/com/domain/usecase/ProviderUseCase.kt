package com.example.com.domain.usecase

import com.example.com.data.persistence.models.UsuarioTable
import com.example.com.data.persistence.repository.PersistenceMusicaRepository
import com.example.com.data.persistence.repository.PersistenceUsuarioRepository
import com.example.com.data.secutiry.PasswordHash
import com.example.com.domain.models.Musica
import com.example.com.domain.models.UpdateMusica
import com.example.com.domain.models.UpdateUsuario
import com.example.com.domain.models.Usuario
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ProviderUseCase {
    private val repository = PersistenceMusicaRepository()
    private val repositoryUsuario = PersistenceUsuarioRepository()
    val logger: Logger = LoggerFactory.getLogger("UseCaseLogger")

    private val getAllMusicaUseCase = GetAllMusicaUseCase(repository)
    private val getMusicaByNameUseCase = GetMusicaByNameUseCase(repository)
    private val updateMusicaUseCase = UpdateMusicaUseCase(repository)
    private val insertMusicaUseCase = InsertMusicaUseCase(repository)
    private val deleteMusicaUseCase = DeleteMusicaUseCase(repository)
    private val loginUseCase = LoginUseCase(repositoryUsuario)
    private val registerUseCase = RegisterUseCase(repositoryUsuario)

    // Método para obtener todas las músicas.
    suspend fun getAllMusica() = getAllMusicaUseCase()

    // Método para obtener música por nombre.
    suspend fun getMusicaByName(name: String): List<Musica> {
        if (name.isNullOrBlank()) {
            logger.warn("El nombre está vacío. No podemos buscar la música.")
            return emptyList()
        }

        val musica = getMusicaByNameUseCase(name)
        return if (musica.isEmpty()) {
            logger.warn("No se ha encontrado música con el nombre $name.")
            emptyList()
        } else {
            musica
        }
    }

    // Método para insertar música.
    suspend fun insertMusica(musica: Musica?): Boolean {
        if (musica == null) {
            logger.warn("No existen datos de la música para insertar.")
            return false
        }
        insertMusicaUseCase.musica = musica
        val res = insertMusicaUseCase()
        return if (!res) {
            logger.warn("No se ha insertado la música. Posiblemente ya exista.")
            false
        } else {
            true
        }
    }

    // Método para actualizar música.
    suspend fun updateMusica(updateMusica: UpdateMusica, name: String): Boolean? {
        return try {
            updateMusicaUseCase(updateMusica, name)
        } catch (e: Exception) {
            logger.error("Error al actualizar la música/artista: ${e.localizedMessage}")
            false
        }
    }

    //Método para eliminar música
    suspend fun deleteMusica(name: String): Boolean {
        return deleteMusicaUseCase(name)
    }

    //Método login usuario
    suspend fun login(nombre: String?, pass: String?) : Usuario? = loginUseCase(nombre, pass)

    suspend fun register(usuario: UpdateUsuario): Usuario? {
        return if (
            usuario.nombre.isNullOrBlank() ||
            usuario.contrasena.isNullOrBlank()
        ) {
            null
        } else {
            registerUseCase(usuario)
        }
    }

    fun getUsuarioByNombre(nombre: String): Usuario? {
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
}


