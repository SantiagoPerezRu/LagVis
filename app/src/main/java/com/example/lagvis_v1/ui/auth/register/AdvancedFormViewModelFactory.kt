// ui/auth/AdvancedFormViewModelFactory.kt
package com.example.lagvis_v1.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProviderKt
import com.example.lagvis_v1.data.remote.AdvancedRegisterApiKt
import com.example.lagvis_v1.data.repository.AdvancedRegisterRepositoryImplKt
import com.example.lagvis_v1.dominio.repositorio.auth.AdvancedRegisterRepositoryKt

class AdvancedFormViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdvancedFormViewModel::class.java)) {
            val api: AdvancedRegisterApiKt = RetroFitProviderKt.provideAdvancedRegisterApi()
            val repo: AdvancedRegisterRepositoryKt = AdvancedRegisterRepositoryImplKt(api)
            return AdvancedFormViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
