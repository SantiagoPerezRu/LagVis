package com.example.lagvis_v1.dominio.repositorio

interface ConvenioRepository {

    /** ¿Existe ya cacheado el archivo? */
    suspend fun isCached(archivo: String): Boolean

    /** Garantiza que el archivo está cacheado: si falta, lo descarga y guarda. */
    suspend fun ensureCached(archivo: String)

    /** Lee el XML local ya cacheado y lo devuelve como String. */
    suspend fun readLocalXml(archivo: String): String

}