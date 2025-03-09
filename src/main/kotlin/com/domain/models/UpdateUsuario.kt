package com.example.com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsuario(
    var nombre: String? = null,
    var contrasena: String? = null,
    var token:String? = null
)