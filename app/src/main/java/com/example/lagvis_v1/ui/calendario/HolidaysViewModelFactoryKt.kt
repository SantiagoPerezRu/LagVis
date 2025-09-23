// ui/calendario/HolidaysViewModelFactoryKt.kt
package com.example.lagvis_v1.ui.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.core.network.RetroFitProviderKt
import com.example.lagvis_v1.data.repository.HolidaysRepositoryImplKt
import com.example.lagvis_v1.dominio.repositorio.HolidayRepositoryKt

class HolidaysViewModelFactoryKt : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetroFitProviderKt.holidaysApi()
        val repo: HolidayRepositoryKt = HolidaysRepositoryImplKt(api)
        return HolidaysViewModelKt(repo) as T
    }
}
