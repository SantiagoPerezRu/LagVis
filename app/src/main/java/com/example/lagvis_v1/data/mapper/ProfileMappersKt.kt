// file: com/example/lagvis_v1/data/mapper/ProfileMappers.kt
package com.example.lagvis_v1.data.mapper

import com.example.lagvis_v1.data.remote.dto.user.UserResponseDto
import com.example.lagvis_v1.dominio.model.UserProfileKt

fun UserResponseDto?.toDomain(): UserProfileKt? {
    val datos = this?.datos ?: return null
    if (datos.isEmpty()) return null
    val d = datos[0] ?: return null

    return UserProfileKt(
        d.nombre ?: "",
        d.apellido ?: "",
        d.apellido2 ?: "",
        d.sector_laboral ?: "",
        d.comunidad_autonoma ?: "",
        d.fecha_nacimiento ?: ""
    )
}
