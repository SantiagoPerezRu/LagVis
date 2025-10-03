// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioViewModelFactory.kt
package com.example.lagvis_v1.ui.convenio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProvider // usa tu nombre real
import com.example.lagvis_v1.data.remote.RatingsApi
import com.example.lagvis_v1.data.repository.RatingsRepositoryImpl
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository

class ConvenioViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api: RatingsApi = RetroFitProvider.provideRatingsApi()
        val repo: RatingsRepository = RatingsRepositoryImpl(api)
        return ConvenioViewModel(repo) as T
    }
}
