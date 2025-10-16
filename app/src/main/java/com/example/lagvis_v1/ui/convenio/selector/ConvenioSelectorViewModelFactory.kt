// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioSelectorViewModelFactory.kt
package com.example.lagvis_v1.ui.convenio.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConvenioSelectorViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConvenioSelectorViewModel() as T
    }
}
