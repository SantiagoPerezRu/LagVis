package com.example.lagvis_v1.dominio.repositorio.holidays

import com.example.lagvis_v1.dominio.model.holidays.PublicHolidayKt
import com.example.lagvis_v1.dominio.model.Result

interface HolidayRepositoryKt {
    suspend fun getHolidaysByProvince(
        year: Int,
        provinciaSlug: String
    ): Result<List<PublicHolidayKt>>
}