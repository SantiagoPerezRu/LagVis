package com.example.lagvis_v1.data.repository

import com.example.lagvis_v1.BuildConfig
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
    ): Result<Void> = try {
        val url = com.example.lagvis_v1.core.util.LagVisConstantes.ENDPOINT_GUARDAR_NOTICIA // debe ser URL absoluta
        val resp = saveApi.save(
            url = url,
            uid = uid,
            title = item.title,
            pubDate = item.pubDate,     // tus campos de dominio son no nulos → ok
            link = item.link,
            creator = item.creator
        )
        if (resp.isSuccessful) {
            Result.Success(null as Void)       // Void -> devuelve null en el Success
        } else {
            Result.Error("HTTP ${resp.code()}: ${resp.errorBody()?.string().orEmpty()}")
        }
    } catch (e: IOException) {
        Result.Error(e.message ?: "Error de red", e)
    } catch (t: Throwable) {
        Result.Error(t.message ?: "Error inesperado", t)
    }
}