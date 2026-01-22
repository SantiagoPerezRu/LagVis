// data/remote/HolidaysApiKt.kt
package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.BuildConfig
import com.example.lagvis_v1.data.remote.dto.holiday.HolidaysResponseKt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface HolidaysApiKt {
    @Headers("x-api-key:"+ BuildConfig.HOLIDAYS_API_KEY)
    @GET("v1/holidays")
    suspend fun getHolidays(
        @Query("slug") slug: String,
        @Query("year") year: Int
    ): Response<HolidaysResponseKt>
}