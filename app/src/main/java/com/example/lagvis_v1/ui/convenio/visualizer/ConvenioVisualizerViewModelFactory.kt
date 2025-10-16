// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioVisualizerViewModelFactory.kt
package com.example.lagvis_v1.ui.convenio.visualizer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProviderKt
import com.example.lagvis_v1.data.repository.ConvenioRepositoryImpl
import com.example.lagvis_v1.dominio.repositorio.convenio.ConvenioRepository

class ConvenioVisualizerViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetroFitProviderKt.provideConveniosApi()
        val repo: ConvenioRepository = ConvenioRepositoryImpl(app, api)
        return ConvenioVisualizerViewModel(repo) as T
    }
}
