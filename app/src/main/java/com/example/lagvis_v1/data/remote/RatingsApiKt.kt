package com.example.lagvis_v1.data.remote

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface RatingsApiKt {
    @FormUrlEncoded
    @POST
    fun submit(
        @Url url: String?,  // LagVisConstantes.ENDPOINT_INSERTAR_VALORACION
        @Field("convenio_id") convenioId: Int,
        @Field("usuario_id") usuarioId: String?,
        @Field("puntuacion") puntuacion: Int
    ): Call<Unit?>?
}