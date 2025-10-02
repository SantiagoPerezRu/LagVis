package com.example.lagvis_v1.core.network

import com.example.lagvis_v1.BuildConfig
import com.example.lagvis_v1.data.remote.AdvancedRegisterApiKt
import com.example.lagvis_v1.data.remote.HolidaysApiKt
import com.example.lagvis_v1.data.remote.NewsApiKt
import com.example.lagvis_v1.data.remote.ProfileApi
import com.example.lagvis_v1.data.remote.ProfileApiKt
import com.example.lagvis_v1.data.remote.RatingsApi
import com.example.lagvis_v1.data.remote.RatingsApiKt
import com.example.lagvis_v1.data.remote.SaveNewsApiKt
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitProviderKt {

    private fun retrofit(baseUrl: String) = Retrofit.Builder()
        .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun newsApi(): NewsApiKt =
        retrofit(BuildConfig.NEWS_BASE_URL).create(NewsApiKt::class.java)

    fun saveNewsApi(): SaveNewsApiKt =
        retrofit(BuildConfig.BACKEND_BASE_URL).create(SaveNewsApiKt::class.java)

    fun holidaysApi(): HolidaysApiKt =
        retrofit(BuildConfig.HOLIDAYS_BASE_URL).create(HolidaysApiKt::class.java)

    fun provideAdvancedRegisterApi(): AdvancedRegisterApiKt =
        retrofit(BuildConfig.BACKEND_BASE_URL).create(AdvancedRegisterApiKt::class.java)

    val profileApi: ProfileApiKt by lazy {
        retrofit(BuildConfig.BACKEND_BASE_URL).create(ProfileApiKt::class.java)
    }

    fun ratingsApi(): RatingsApiKt =
        retrofit(BuildConfig.BACKEND_BASE_URL).create(RatingsApiKt::class.java)
}