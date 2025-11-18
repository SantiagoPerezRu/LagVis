// file: app/src/main/java/com/example/lagvis_v1/ui/common/LookupViewModel.kt
package com.example.lagvis_v1.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.data.repository.LookupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LookupViewModel(
    repo: LookupRepository
) : ViewModel() {

    val comunidades: StateFlow<List<UiItem>> =
        repo.observeComunidades()
            .map { list -> list.map { UiItem(id = it.id, nombre = it.nombre) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sectores: StateFlow<List<UiItem>> =
        repo.observeSectores()
            .map { list -> list.map { UiItem(id = it.id, nombre = it.nombre) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // (Opcional) si aún quieres los nombres sueltos:
    val comunidadesUi: StateFlow<List<String>> =
        comunidades.map { it.map(UiItem::nombre) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sectoresUi: StateFlow<List<String>> =
        sectores.map { it.map(UiItem::nombre) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
