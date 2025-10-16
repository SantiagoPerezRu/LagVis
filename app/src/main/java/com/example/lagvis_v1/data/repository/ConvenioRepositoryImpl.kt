// file: app/src/main/java/com/example/lagvis_v1/data/repository/ConvenioRepositoryImpl.kt
package com.example.lagvis_v1.data.repository

import android.content.Context
import android.util.Log
import com.example.lagvis_v1.data.remote.ConveniosApi
import com.example.lagvis_v1.dominio.repositorio.convenio.ConvenioRepository
import okio.buffer
import okio.sink
import java.io.File
import org.xmlpull.v1.XmlPullParserFactory
import kotlin.runCatching  // 👈 asegúrate de tener este import

class ConvenioRepositoryImpl(
    private val appContext: Context,
    private val api: ConveniosApi
) : ConvenioRepository {

    companion object { private const val TAG = "ConvenioRepo" }

    private val baseDir by lazy {
        File(appContext.filesDir, "convenios").apply {
            if (!exists()) {
                val created = mkdirs()
                Log.d(TAG, "Creando baseDir: $absolutePath -> creado=$created")
            } else {
                Log.d(TAG, "Usando baseDir existente: $absolutePath")
            }
        }
    }

    private fun fileFor(archivo: String) = File(baseDir, archivo)

    override suspend fun isCached(archivo: String): Boolean = fileFor(archivo).exists()

    override suspend fun ensureCached(archivo: String) {
        val target = fileFor(archivo)
        if (target.exists()) {
            Log.d(TAG, "Cache ya presente: ${target.absolutePath} (size=${target.length()} bytes)")
            return
        }

        // 👇 AHORA ES suspend (puede llamar a api.getFile)
        suspend fun writeOnce(): Pair<Long, Long?> {
            val resp = api.getFile(archivo)
            Log.d(TAG, "HTTP getFile('$archivo') -> code=${resp.code()} success=${resp.isSuccessful}")
            val body = resp.body() ?: error("Respuesta sin cuerpo (HTTP ${resp.code()})")

            val headerLen = body.contentLength().takeIf { it >= 0 } // -1 si desconocido

            val written = runCatching {
                body.source().use { source ->
                    target.sink().buffer().use { sink ->
                        val n = sink.writeAll(source) // bytes escritos
                        sink.flush()
                        n
                    }
                }
            }.getOrElse { e ->
                // Limpia archivo parcial si falla
                runCatching { target.delete() }
                throw e
            }

            Log.d(TAG, "Guardado '$archivo' en ${target.absolutePath} (bytesEscritos=$written, contentLength=$headerLen)")
            return written to headerLen
        }

        // 1º intento
        val (written1, header1) = writeOnce()

        // Verifica tamaño si el servidor lo indica
        val sizeOk = header1 == null || written1 == header1
        if (!sizeOk) {
            Log.w(TAG, "Tamaño inconsistente (written=$written1, header=$header1). Reintentando...")
            runCatching { target.delete() }
            val (written2, header2) = writeOnce()
            if (header2 != null && written2 != header2) {
                throw IllegalStateException("Descarga corrupta tras reintento (written=$written2, header=$header2)")
            }
        }

        // Validación rápida: XML bien formado
        if (!isWellFormedXml(target)) {
            Log.w(TAG, "XML mal formado. Guardando copia .bad y reintentando una vez...")
            val bad = File(target.parentFile, "${target.name}.bad")
            runCatching { target.copyTo(bad, overwrite = true) }
            runCatching { target.delete() }
            val (written3, header3) = writeOnce()
            if (header3 != null && written3 != header3) {
                throw IllegalStateException("Descarga corrupta tras reintento final (written=$written3, header=$header3)")
            }
            if (!isWellFormedXml(target)) {
                throw IllegalStateException("XML mal formado tras reintento final. Revisa ${bad.absolutePath}")
            }
        }
    }

    override suspend fun readLocalXml(archivo: String): String {
        val f = fileFor(archivo)
        if (!f.exists()) error("Archivo no cacheado: $archivo")
        Log.d(TAG, "Leyendo XML local desde: ${f.absolutePath} (size=${f.length()} bytes)")
        return f.readText()
    }

    // --- helpers ---

    private fun isWellFormedXml(file: File): Boolean = try {
        file.inputStream().use { input ->
            val parser = XmlPullParserFactory.newInstance()
                .apply { isNamespaceAware = false }
                .newPullParser().also { it.setInput(input, null) }

            while (parser.eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                parser.next()
            }
        }
        true
    } catch (t: Throwable) {
        Log.e(TAG, "XML inválido: ${t.message}")
        false
    }
}
