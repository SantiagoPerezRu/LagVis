package com.example.lagvis_v1.core.network

import android.util.Log
import com.example.lagvis_v1.BuildConfig
import com.example.lagvis_v1.data.remote.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroFitProviderKt {

    // ---------- Client con logging para TODAS las peticiones ----------
    private val loggingClient: OkHttpClient by lazy { buildLoggingClient() }

    private fun buildLoggingClient(): OkHttpClient {
        // Interceptor básico: URL completa + timing + código
        val urlLogger = Interceptor { chain ->
            val request = chain.request()
            val t1 = System.nanoTime()
            Log.d("HTTP", "→ ${request.method} ${request.url}") // URL completa con query

            val response: Response = chain.proceed(request)

            val t2 = System.nanoTime()
            val ms = (t2 - t1) / 1_000_000.0
            Log.d(
                "HTTP",
                "← ${response.code} ${response.message} (${String.format("%.1f", ms)} ms) for ${request.url}"
            )
            response
        }

        // Interceptor opcional de OkHttp (headers/body). Ajusta el nivel si quieres menos ruido.
        val bodyLogger = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }.apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(urlLogger)
            .addInterceptor(bodyLogger)
            .build()
    }

    // ---------- Retrofit helper que usa SIEMPRE el loggingClient ----------
    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(loggingClient)
            .build()

    // ---------- APIS (todas pasan por el mismo cliente con logs) ----------
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

    fun provideConveniosApi(): ConveniosApi =
        retrofit(BuildConfig.CONVENIOS_BASE_URL).create(ConveniosApi::class.java)
}
