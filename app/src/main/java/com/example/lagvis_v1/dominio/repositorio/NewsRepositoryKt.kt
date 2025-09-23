package com.example.lagvis_v1.dominio.repositorio

import com.example.lagvis_v1.dominio.model.NewsItemKt
import com.example.lagvis_v1.dominio.model.Result

interface NewsRepositoryKt {

    suspend fun fetchNews(
         query: String,
         country: String,
         category: String
    ): Result<List<NewsItemKt>>


    suspend fun saveNew(
        uid: String,
        item: NewsItemKt
    ): Result<Void>
}