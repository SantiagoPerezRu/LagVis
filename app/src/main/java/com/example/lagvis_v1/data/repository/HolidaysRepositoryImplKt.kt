// data/repository/HolidaysRepositoryImplKt.kt
package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.data.mapper.toDomain
import com.example.lagvis_v1.data.remote.HolidaysApiKt
import com.example.lagvis_v1.data.remote.dto.holiday.HolidaysResponseKt
import com.example.lagvis_v1.dominio.model.holidays.PublicHolidayKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.repositorio.holidays.HolidayRepositoryKt
import retrofit2.Response
import java.io.IOException

class HolidaysRepositoryImplKt(
    private val api: HolidaysApiKt
) : HolidayRepositoryKt {

    override suspend fun getHolidaysByProvince(
        year: Int,
        provinciaSlug: String
    ): Result<List<PublicHolidayKt>> = try {
        val resp: Response<HolidaysResponseKt> = api.getHolidays(provinciaSlug, year)
        if (resp.isSuccessful) {
            val body = resp.body() ?: return Result.Error("Respuesta vacía")
            Result.Success(body.toDomain())
        } else {
            val msg = resp.errorBody()?.string().orEmpty().ifBlank { "sin cuerpo" }
            Result.Error("HTTP ${resp.code()}: $msg")
        }
    } catch (e: IOException) {
        Result.Error(e.message ?: "Error de red", e)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Error inesperado", t)
    }
}
