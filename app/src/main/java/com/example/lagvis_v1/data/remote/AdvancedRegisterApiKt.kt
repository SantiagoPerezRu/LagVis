package com.example.lagvis_v1.data.remote

import UserResponseKt
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AdvancedRegisterApiKt {
    @FormUrlEncoded
    @POST("insertar_.php")
    suspend fun insert(
        @Field("uid") uid: String,
        @Field("nombre") nombre: String,
        @Field("apellido") apellido1: String,
        @Field("apellido2") apellido2: String,
        @Field("comunidad_id") comunidadId: String,
        @Field("sector_id") sectorId: String,
        @Field("fechaNacimiento") fechaNacimiento: String
    ): Response<UserResponseKt>
}
