package com.example.com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    var id: Int? = null,
    var nombre: String,
    var contrasena: String,
    var token: String ? = null
)