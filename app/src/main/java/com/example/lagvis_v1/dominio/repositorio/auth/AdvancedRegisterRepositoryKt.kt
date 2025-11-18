package com.example.lagvis_v1.dominio.repositorio.auth

import com.example.lagvis_v1.dominio.model.Result

interface AdvancedRegisterRepositoryKt {



    suspend fun insert(
        uid: String,
        nombre: String,
        apellido1: String,
        apellido2: String,
        comunidadId: String,
        sectorId: String,
        fechaNacimiento: String
    ): Result<Unit>


}