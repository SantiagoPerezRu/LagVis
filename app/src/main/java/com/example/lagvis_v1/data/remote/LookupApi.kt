package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.data.remote.dto.util.LookupDtos
import retrofit2.Response
import retrofit2.http.GET

interface LookupApi {
    @GET("get_comunidades.php")
    suspend fun getComunidades(): Response<LookupDtos.SimpleLookupResponse>

    @GET("get_sectores.php")
    suspend fun getSectores(): Response<LookupDtos.SimpleLookupResponse>
}
