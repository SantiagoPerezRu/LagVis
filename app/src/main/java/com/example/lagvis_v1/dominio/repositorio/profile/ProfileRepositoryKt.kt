package com.example.lagvis_v1.dominio.repositorio.profile

import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.model.profile.UserProfileKt

interface ProfileRepositoryKt {

    suspend fun getProfileData(
        uid: String
    ): Result<UserProfileKt>

}