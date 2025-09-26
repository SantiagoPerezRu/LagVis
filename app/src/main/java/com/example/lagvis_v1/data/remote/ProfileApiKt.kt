// file: com/example/lagvis_v1/data/remote/ProfileApi.kt
package com.example.lagvis_v1.data.remote

import com.example.lagvis_v1.data.remote.dto.user.UserResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProfileApiKt {
    @FormUrlEncoded
    @POST("mostrar_.php")
    suspend fun getProfileData(
        @Field("uid") uid: String
    ): Response<UserResponseDto>
}
