// file: app/src/main/java/com/example/lagvis_v1/ui/conveniosIa/ConveniosIaViewModelFactory.kt
package com.example.lagvis_v1.ui.conveniosIa

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.data.repository.ia.GeminiConvenioRepository

class ConveniosIaViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConveniosIaViewModel::class.java)) {
            // Inicializa el Repositorio de la IA
            val repo = GeminiConvenioRepository(app.applicationContext)
            return ConveniosIaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}