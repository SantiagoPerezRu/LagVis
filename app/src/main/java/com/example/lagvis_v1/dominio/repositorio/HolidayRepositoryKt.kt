// dominio/repositorio/HolidayRepositoryKt.kt
package com.example.lagvis_v1.dominio.repositorio

import com.example.lagvis_v1.dominio.model.PublicHolidayKt
import com.example.lagvis_v1.dominio.model.Result

interface HolidayRepositoryKt {
    suspend fun getHolidaysByProvince(
        year: Int,
        provinciaSlug: String
    ): Result<List<PublicHolidayKt>>
}
