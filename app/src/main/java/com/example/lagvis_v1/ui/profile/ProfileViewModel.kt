package com.example.lagvis_v1.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.Result
import com.example.lagvis_v1.dominio.model.profile.UserProfileKt
import com.example.lagvis_v1.dominio.repositorio.profile.ProfileRepositoryKt
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: ProfileRepositoryKt
) : ViewModel() {

    private val _state = MutableLiveData<UiState<UserProfileKt>>(UiState.Loading())
    val state: LiveData<UiState<UserProfileKt>> = _state

    fun getProfileData(uid: String) {
        if (uid.isBlank()) {
            _state.value = UiState.Error("UID vacío")
            return
        }

        _state.value = UiState.Loading()

        viewModelScope.launch {
            try {
                when (val result = repo.getProfileData(uid)) {
                    is Result.Success -> {
                        val user = result.data
                        if (user != null) {
                            _state.postValue(UiState.Success(user))
                        } else {
                            _state.postValue(UiState.Error("Datos de usuario vacíos"))
                        }
                    }
                    is Result.Error -> {
                        _state.postValue(UiState.Error(result.message ?: "Error desconocido"))
                    }
                }
            } catch (t: Throwable) {
                _state.postValue(UiState.Error(t.message ?: "Error inesperado"))
            }
        }
    }
}
