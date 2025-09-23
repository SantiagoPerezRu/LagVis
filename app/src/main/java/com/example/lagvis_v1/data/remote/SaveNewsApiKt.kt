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
    @POST("guardar_noticia.php")
    suspend fun save(
        @Field("uid") uid: String,
        @Field("titulo") titulo: String,
        @Field("fecha") fecha: String,
        @Field("enlace") enlace: String,
        @Field("creador") creador: String?
    ): Response<Void>

}