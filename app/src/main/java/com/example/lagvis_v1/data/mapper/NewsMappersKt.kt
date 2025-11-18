package com.example.lagvis_v1.data.mapper

import com.example.lagvis_v1.data.remote.dto.news.NewsItemDtoKt
import com.example.lagvis_v1.data.remote.dto.news.NewsResponseKt
import com.example.lagvis_v1.dominio.model.news.NewsItemKt


fun NewsResponseKt.toDomain(): List<NewsItemKt> =
    (results ?: emptyList()).map { it.toDomain() }

fun NewsItemDtoKt.toDomain(): NewsItemKt =
    NewsItemKt(
        title = title.orEmpty(),
        pubDate = pubDate.orEmpty(),
        link = link.orEmpty(),
        creator = creator.orEmpty()
    )