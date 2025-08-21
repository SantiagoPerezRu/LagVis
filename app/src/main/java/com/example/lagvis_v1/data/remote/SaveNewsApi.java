package com.example.lagvis_v1.data.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface SaveNewsApi {
    @FormUrlEncoded
    @POST
    Call<Void> save(
            @Url String url,          // ← pasas la URL completa (LagVisConstantes.ENDPOINT_GUARDAR_NOTICIA)
            @Field("uid") String uid,
            @Field("titulo") String titulo,
            @Field("fecha") String fecha,
            @Field("enlace") String enlace,
            @Field("creador") String creador
    );
}
