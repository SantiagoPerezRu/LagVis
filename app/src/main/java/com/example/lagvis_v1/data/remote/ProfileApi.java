package com.example.lagvis_v1.data.remote;

import com.example.lagvis_v1.data.remote.dto.user.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ProfileApi {
    @FormUrlEncoded
    @POST
    Call<Void> insert(
            @Url String url,           // p.ej. LagVisConstantes.ENDPOINT_INSERTAR (URL completa)
            @Field("uid") String uid,
            @Field("nombre") String nombre,
            @Field("apellido") String apellido1,
            @Field("apellido2") String apellido2,
            @Field("comunidad_id") String comunidadId,
            @Field("sector_id") String sectorId,
            @Field("fechaNacimiento") String fechaNacimiento
    );

    @FormUrlEncoded
    @POST
    Call<UserResponse> show(
            @Url String url,       // LagVisConstantes.ENDPOINT_MOSTRAR (URL completa)
            @Field("uid") String uid
    );
}
