package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.data.remote.dto.news.NewsResponseKt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiKt {

    @GET("news")
    suspend fun getNews(
        @Query("apikey") apiKey: String,
        @Query("q") query: String,
        @Query("country") country: String,
        @Query("category")category: String
    ): Response<NewsResponseKt>

}