// data/repository/AdvancedRegisterRepositoryImplKt.kt
package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.data.remote.AdvancedRegisterApiKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.repositorio.AdvancedRegisterRepositoryKt
import okhttp3.ResponseBody

class AdvancedRegisterRepositoryImplKt(
    private val api: AdvancedRegisterApiKt
) : AdvancedRegisterRepositoryKt {

    override suspend fun insert(
        uid: String,
        nombre: String,
        apellido1: String,
        apellido2: String,
        comunidadId: String,
        sectorId: String,
        fechaNacimiento: String
    ): Result<Unit> = try {
        val resp = api.insert(uid, nombre, apellido1, apellido2, comunidadId, sectorId, fechaNacimiento)

        if (resp.isSuccessful) {
            val body = resp.body()
            // toleramos distintos "éxitos" por si el backend no es consistente
            val ok = body?.exito == "1" ||
                    body?.exito?.equals("ok", true) == true ||
                    body?.exito?.equals("true", true) == true
            if (ok) Result.Success(Unit)
            else Result.Error(body?.mensaje ?: "Respuesta de aplicación no OK (exito=${body?.exito})")
        } else {
            val raw = safeBody(resp.errorBody())
            Result.Error("HTTP ${resp.code()} ${resp.message()} | $raw")
        }
    } catch (t: Throwable) {
        Result.Error("Excepción: ${t.message ?: t::class.java.simpleName}")
    }

    private fun safeBody(b: ResponseBody?): String =
        try { b?.string().orEmpty().take(500) } catch (_: Throwable) { "" }
}
