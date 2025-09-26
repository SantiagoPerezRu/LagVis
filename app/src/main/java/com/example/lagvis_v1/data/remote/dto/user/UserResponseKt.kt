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
    val sector_laboral: String? = null,
    val comunidad_autonoma: String? = null,
    val fecha_nacimiento: String? = null
)