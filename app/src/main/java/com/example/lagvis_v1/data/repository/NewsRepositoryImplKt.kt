package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.BuildConfig
import com.example.lagvis_v1.core.util.LagVisConstantes
import com.example.lagvis_v1.data.mapper.toDomain
import com.example.lagvis_v1.data.remote.NewsApiKt
import com.example.lagvis_v1.data.remote.SaveNewsApiKt
import com.example.lagvis_v1.dominio.model.NewsItemKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.repositorio.NewsRepositoryKt
import retrofit2.Response
import java.io.IOException

class NewsRepositoryImplKt(
    private val api: NewsApiKt,
    private val saveApi: SaveNewsApiKt,
    private val apiKey: String = BuildConfig.API_KEY_NEWS

) : NewsRepositoryKt {


    override suspend fun fetchNews(
        query: String,
        country: String,
        category: String
    ): Result<List<NewsItemKt>> = try {
        val resp = api.getNews(apiKey, query.orEmpty(), country.orEmpty(), category.orEmpty())
        if (resp.isSuccessful) {
            val body = resp.body() ?: return Result.Error("Respuesta vacía")
            Result.Success(body.toDomain())
        } else {
            Result.Error("HTTP ${resp.code()}: ${resp.errorBody()?.string().orEmpty()}")
        }
    } catch (e: IOException) {
        Result.Error(e.message ?: "Error de red", e)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Error inesperado", t)
    }


    override suspend fun saveNew(
        uid: String,
        item: NewsItemKt
    ): Result<Unit> = try {
        val url = LagVisConstantes.ENDPOINT_GUARDAR_NOTICIA  // absoluta si usas @Url
        val resp = saveApi.save(
            uid = uid,
            titulo = item.title,                  // <- CAMBIA a 'titulo'
            fecha = item.pubDate,                // <- CAMBIA a 'fecha' (string)
            enlace = item.link,                  // <- CAMBIA a 'enlace'
            creador = item.creator ?: ""         // PHP lo maneja opcional
        )
        if (resp.isSuccessful) {
            Result.Success(Unit)   // ✅ nada que castear
        } else {
            val err = resp.errorBody()?.string().orEmpty()
            Result.Error("HTTP ${resp.code()} ${resp.message()}: $err")
        }
    } catch (e: IOException) {
        Result.Error("Error de red: ${e.message}", e)
    } catch (t: Throwable) {
        Result.Error("Error inesperado: ${t.message}", t)
    }
}