package com.example.lagvis_v1.data.remote.dto.news

import com.example.lagvis_v1.data.remote.adapters.CreatorAdapter
import com.google.gson.annotations.JsonAdapter

data class NewsResponseKt(
    val results: List<NewsItemDtoKt>?
)

data class NewsItemDtoKt(
    val title: String?,
    val pubDate: String?,
    val link: String?,
    @JsonAdapter(CreatorAdapter::class)
    val creator: String?
)