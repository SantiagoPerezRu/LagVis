// data/repository/LookupRepository.kt
package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.data.local.dao.LookupDao
import com.example.lagvis_v1.data.local.entity.ComunidadEntity
import com.example.lagvis_v1.data.local.entity.SectorEntity
import com.example.lagvis_v1.data.remote.LookupApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LookupRepository(
    private val api: LookupApi,
    private val dao: LookupDao,
    private val timeProvider: () -> Long = { System.currentTimeMillis() }
) {
    // TTL (ej. 7 días). Ajusta a tu gusto.
    private val ttlMs = 7 * 24 * 60 * 60 * 1000L

    // Guarda el último refresh en memoria (puedes moverlo a DataStore si quieres persistir el timestamp)
    private var lastRefreshAt: Long = 0L

    fun observeComunidades(): Flow<List<ComunidadEntity>> = dao.observeComunidades()
    fun observeSectores(): Flow<List<SectorEntity>> = dao.observeSectores()

    suspend fun refreshOnAppStart(force: Boolean = false) {
        val now = timeProvider()
        val shouldRefresh = force || (now - lastRefreshAt > ttlMs)

        val haveLocal =
            dao.getComunidades().isNotEmpty() && dao.getSectores().isNotEmpty()

        if (!haveLocal || shouldRefresh) {
            // Comunidades
            val cResp = api.getComunidades()
            if (cResp.isSuccessful) {
                val body = cResp.body()
                if (body?.success == true) {
                    val items = body.data.map { ComunidadEntity(id = it.id, nombre = it.nombre) }
                    dao.clearComunidades()
                    dao.upsertComunidades(items)
                }
            }
            // Sectores
            val sResp = api.getSectores()
            if (sResp.isSuccessful) {
                val body = sResp.body()
                if (body?.success == true) {
                    val items = body.data.map { SectorEntity(id = it.id, nombre = it.nombre) }
                    dao.clearSectores()
                    dao.upsertSectores(items)
                }
            }
            lastRefreshAt = now
        }
    }
}
