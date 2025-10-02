package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.core.util.LagVisConstantes
import com.example.lagvis_v1.data.remote.RatingsApi
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository
import java.io.IOException

class RatingsRepositoryImplKt(private val api: RatingsApi) : RatingsRepository {
    override fun rate(
        convenioId: Int,
        userId: String?,
        puntuacion: Int
    ): RatingsRepository.Result<Void?> {
        try {
            val r = api.submit(
                LagVisConstantes.ENDPOINT_INSERTAR_VALORACION,
                convenioId, userId, puntuacion
            ).execute()
            return if (r.isSuccessful()) RatingsRepository.Result.success<Void?>(null) else RatingsRepository.Result.error<Void?>(
                "HTTP " + r.code()
            )
        } catch (e: IOException) {
            return RatingsRepository.Result.error<Void?>(e.message)
        }
    }
}