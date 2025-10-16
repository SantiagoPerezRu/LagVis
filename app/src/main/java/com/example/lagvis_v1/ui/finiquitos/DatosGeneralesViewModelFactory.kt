// app/src/main/java/com/example/lagvis_v1/ui/finiquitos/DatosGeneralesFiniquitoViewModelFactory.kt
package com.example.lagvis_v1.ui.finiquitos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DatosGeneralesFiniquitoViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatosGeneralesFiniquitoViewModel::class.java)) {
            return DatosGeneralesFiniquitoViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
