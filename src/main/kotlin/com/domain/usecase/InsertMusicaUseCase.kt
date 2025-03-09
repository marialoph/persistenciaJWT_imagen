package com.example.com.domain.usecase

import com.example.com.domain.infraestructura.Utils
import com.example.com.domain.models.Musica
import com.example.com.domain.repository.MusicaInterface

class InsertMusicaUseCase(private val repository: MusicaInterface) {
    var musica: Musica? = null
    suspend operator fun invoke(): Boolean {
        if (musica == null) {
            return false
        }
        val musicaConImagen = musica!!
        val dirCreated = Utils.createDir(musicaConImagen.nombre)
        if (!dirCreated) {
            return false
        }
        if (!musicaConImagen.image.isNullOrEmpty()) {
            val imageFileName = Utils.createBase64ToImg(musicaConImagen.image!!, musicaConImagen.nombre)
            if (imageFileName == null) {
                return false
            }
            musicaConImagen.image = imageFileName
        }
        return try {
            repository.postMusica(musicaConImagen)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
