// ui/auth/AdvancedFormViewModel.kt
package com.example.lagvis_v1.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.ui.UiState.*
import com.example.lagvis_v1.dominio.repositorio.AdvancedRegisterRepositoryKt
import kotlinx.coroutines.launch
import com.example.lagvis_v1.dominio.model.Result

class AdvancedFormViewModel(
    private val repo: AdvancedRegisterRepositoryKt
) : ViewModel() {

    private val _submit = MutableLiveData<UiState<Unit>>()
    val submit: LiveData<UiState<Unit>> = _submit

    fun send(
        uid: String,
        nombre: String,
        apellido1: String,
        apellido2: String,
        comunidadId: String,   // IDs como String (como acordaste)
        sectorId: String,
        fechaNacimiento: String
    ) {
        _submit.value = UiState.Loading()
        viewModelScope.launch {
            when (val r = repo.insert(uid, nombre, apellido1, apellido2, comunidadId, sectorId, fechaNacimiento)) {
                is Result.Success<*> -> _submit.value = Success(Unit)
                is Result.Error   -> _submit.value = Error(r.message)

            }
        }
    }
}
