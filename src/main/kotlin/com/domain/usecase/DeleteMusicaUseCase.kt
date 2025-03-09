package com.example.com.domain.usecase

import com.example.com.domain.infraestructura.Utils
import com.example.com.domain.repository.MusicaInterface

class DeleteMusicaUseCase(private val repository: MusicaInterface) {

    suspend operator fun invoke(name: String): Boolean {
        try {
            val imageName = "$name.jpg"

            val imageDeleted = Utils.deleteImage(name, imageName)

            if (!imageDeleted) {
                return false
            }

            val directoryDeleted = Utils.deleteDirectory(name)
            if (!directoryDeleted) {
                return false
            }

            return repository.deleteMusica(name)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
