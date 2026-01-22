package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.data.remote.dto.user.UserResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AdvancedRegisterApiKt {
    @FormUrlEncoded
    @POST("insertar_usuario")
    suspend fun insert(
        @Field("uid") uid: String,
        @Field("nombre") nombre: String,
        @Field("apellido") apellido1: String,
        @Field("apellido2") apellido2: String,
        @Field("comunidad") comunidadId: String,
        @Field("sector") sectorId: String,
        @Field("fechaNacimiento") fechaNacimiento: String
    ): Response<UserResponseDto>
}
