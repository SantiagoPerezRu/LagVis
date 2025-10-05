// ui/news/NewsViewModelKt.kt
package com.example.lagvis_v1.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.NewsItemKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.repositorio.NewsRepositoryKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsViewModelKt(
    private val repo: NewsRepositoryKt
) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<NewsItemKt>>>(UiState.Loading())
    val state: LiveData<UiState<List<NewsItemKt>>> = _state

    private val _save = MutableLiveData<UiState<Unit>>()
    val save: LiveData<UiState<Unit>> = _save

    fun load(query: String, country: String, category: String) {
        _state.value = UiState.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val r = repo.fetchNews(query, country, category)) {
                is Result.Success -> _state.postValue(UiState.Success(r.data))
                is Result.Error   -> _state.postValue(UiState.Error(r.message))
            }
        }
    }

    fun save(uid: String, item: NewsItemKt) {
        _save.value = UiState.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val r = repo.saveNew(uid, item)) {
                is Result.Success -> _save.postValue(UiState.Success(Unit))
                is Result.Error   -> _save.postValue(UiState.Error(r.message))
            }
        }
    }

    fun loadByCategory(
        categoryEs: String?,
        country: String = "es",
        query: String = ""
    ) {
        // Usa tu mapa ES->EN; si no hay match, "other"
        val categoryEn = NewsCategoryTranslations.toEnglish(categoryEs) ?: "other"
        load(query = query, country = country, category = categoryEn)
    }

    /** Por si quieres cargar sin categoría (top headlines del país). */
    fun loadTop(country: String = "es", query: String = "") {
        load(query = query, country = country, category = "")
    }
}
