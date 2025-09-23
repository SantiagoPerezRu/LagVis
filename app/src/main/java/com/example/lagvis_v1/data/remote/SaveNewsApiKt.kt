package com.example.lagvis_v1.data.remote

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface SaveNewsApiKt {
    @FormUrlEncoded
    @POST
    suspend fun save(
        @Url url: String,
        @Query("uid") uid: String,
        @Query("title") title: String,
        @Query("pubDate") pubDate: String?,
        @Query("link") link: String,
        @Query("creator") creator: String?
    ): Response<Void>

}