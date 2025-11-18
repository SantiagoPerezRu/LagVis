// data/remote/HolidaysApiKt.kt
package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.data.remote.dto.holiday.HolidaysResponseKt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HolidaysApiKt {
    @GET("v1/holidays")
    suspend fun getHolidays(
        @Query("slug") slug: String,
        @Query("year") year: Int
    ): Response<HolidaysResponseKt>
}
