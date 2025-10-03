package com.example.lagvis_v1.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ConveniosApi {
    @Streaming
    @GET("{archivo}")
    suspend fun getFile(@Path("archivo") file: String): Response<ResponseBody>
}