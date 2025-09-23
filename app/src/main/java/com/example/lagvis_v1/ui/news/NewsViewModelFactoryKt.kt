// ui/news/NewsViewModelFactoryKt.kt
package com.example.lagvis_v1.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProviderKt
import com.example.lagvis_v1.data.repository.NewsRepositoryImplKt
import com.example.lagvis_v1.dominio.repositorio.NewsRepositoryKt

class NewsViewModelFactoryKt : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetroFitProviderKt.newsApi()
        val saveApi = RetroFitProviderKt.saveNewsApi()
        val repo: NewsRepositoryKt = NewsRepositoryImplKt(api, saveApi)
        return NewsViewModelKt(repo) as T
    }
}
