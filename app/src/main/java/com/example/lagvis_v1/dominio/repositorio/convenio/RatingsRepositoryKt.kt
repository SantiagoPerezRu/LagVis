package com.example.lagvis_v1.dominio.repositorio.convenio

import com.example.lagvis_v1.dominio.model.Result

interface RatingsRepositoryKt {

    suspend fun rate(convenioId: Int, userId: String, puntuacion: Int): Result<Unit>

}