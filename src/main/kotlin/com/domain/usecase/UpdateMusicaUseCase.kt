package com.example.com.domain.usecase

import com.example.com.domain.infraestructura.Utils
import com.example.com.domain.models.Musica
import com.example.com.domain.models.UpdateMusica
import com.example.com.domain.repository.MusicaInterface

class UpdateMusicaUseCase(private val repository: MusicaInterface) {
    suspend operator fun invoke(updateMusica: UpdateMusica, name: String): Boolean? {
        try {
            val newImage = updateMusica.image

            if (!newImage.isNullOrEmpty()) {
                Utils.deleteImage(name,"")

                updateMusica.image = Utils.createBase64ToImg(newImage, name) ?: newImage
            }
            return repository.updateMusica(updateMusica)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

