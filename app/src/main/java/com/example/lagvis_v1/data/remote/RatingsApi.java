package com.example.lagvis_v1.data.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RatingsApi {
    @FormUrlEncoded
    @POST
    Call<Void> submit(
            @Url String url,               // LagVisConstantes.ENDPOINT_INSERTAR_VALORACION
            @Field("convenio_id") int convenioId,
            @Field("usuario_id") String usuarioId,
            @Field("puntuacion") int puntuacion
    );
}
