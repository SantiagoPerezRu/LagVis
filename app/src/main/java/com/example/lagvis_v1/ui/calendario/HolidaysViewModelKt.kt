// ui/calendario/HolidaysViewModelKt.kt
package com.example.lagvis_v1.ui.calendario

import androidx.lifecycle.*
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.holidays.PublicHolidayKt
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.repositorio.holidays.HolidayRepositoryKt
import kotlinx.coroutines.launch

class HolidaysViewModelKt(
    private val repo: HolidayRepositoryKt
) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<PublicHolidayKt>>>(UiState.Loading())
    val state: LiveData<UiState<List<PublicHolidayKt>>> = _state

    fun loadByProvince(year: Int, provinciaSlug: String) {
        if (provinciaSlug.isBlank()) {
            _state.value = UiState.Error("Provincia vacía")
            return
        }
        _state.value = UiState.Loading()
        viewModelScope.launch {
            when (val r = repo.getHolidaysByProvince(year, provinciaSlug)) {
                is Result.Success -> _state.value = UiState.Success(r.data)
                is Result.Error   -> _state.value = UiState.Error(r.message)
            }
        }
    }
}
