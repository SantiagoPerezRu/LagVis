package com.example.lagvis_v1.dominio.repositorio

import android.util.Log
import com.example.lagvis_v1.data.mapper.toDomain
import com.example.lagvis_v1.data.remote.ProfileApiKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.model.UserProfileKt
import com.google.gson.Gson

class ProfileRepositoryImplKt(
    private val api: ProfileApiKt
) : ProfileRepositoryKt {
    private val TAG = "ProfileRepo"
    override suspend fun getProfileData(uid: String): Result<UserProfileKt> {
        return try {
            val resp = api.getProfileData(uid)
            try{
            Log.d(TAG, "HTTP ${resp.code()} ${resp.message()}")
            resp.headers().forEach { h -> Log.d(TAG, "H: ${h.first}=${h.second}") }

            // Cuerpo en éxito
            if (resp.isSuccessful) {
                val dto = resp.body()
                Log.d(TAG, "RAW SUCCESS BODY: ${dto}") // toString del DTO
                // Si quieres ver el JSON crudo:
                Log.d(TAG, "SUCCESS JSON:\n${Gson().toJson(dto)}")

                val domain = dto.toDomain()
                return if (domain != null) Result.Success(domain)
                else Result.Error("Body mapeado a null")
            } else {
                // Body de error (¡solo se puede leer una vez!)
                val errStr = resp.errorBody()?.string().orEmpty()
                Log.e(TAG, "ERROR BODY:\n$errStr")
                Result.Error("HTTP ${resp.code()} ${resp.message()}")
            }
        } catch (t: Throwable) {
            Log.e(TAG, "EXCEPTION: ${t.javaClass.simpleName}: ${t.message}", t)
            Result.Error(t.message ?: "Exception")
        }


        if (!resp.isSuccessful) {
                return Result.Error("HTTP ${resp.code()}: ${resp.message()}")
            }

            val userProfile = resp.body().toDomain()
                ?: return Result.Error("Respuesta vacía o mal formada")

            Result.Success(userProfile)
        } catch (t: Throwable) {
            Result.Error(t.message ?: "Error desconocido", t)
        }
    }
}
