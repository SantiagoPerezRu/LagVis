// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioViewModel.kt
package com.example.lagvis_v1.ui.convenio.visualizer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConvenioViewModel(private val repo: RatingsRepository) : ViewModel() {

    private val _rate = MutableLiveData<UiState<Void?>>()
    val rate: LiveData<UiState<Void?>> = _rate

    fun rateConvenio(convenioId: Int, userId: String, puntuacion: Int) {
        _rate.postValue(UiState.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val r = repo.rate(convenioId, userId, puntuacion)
            if (r.isSuccess()) {
                _rate.postValue(UiState.Success(null))
            } else {
                _rate.postValue(UiState.Error(r.error))
            }
        }
    }
}
