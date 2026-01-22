package com.example.lagvis_v1.data.remote.dto.user

data class UserResponseDto(
    val exito: String?,
    val mensaje: String?,
    val datos: List<UserDataDto?>? = null
)

data class UserDataDto(
    val nombre: String? = null,
    val apellido: String? = null,
    val apellido2: String? = null,
    val sectorLaboral: String? = null,
    val comunidadAutonoma: String? = null,
    val fechaNacimiento: String? = null
)