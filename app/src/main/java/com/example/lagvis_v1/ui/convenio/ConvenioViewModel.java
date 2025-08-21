package com.example.lagvis_v1.ui.convenio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvenioViewModel extends ViewModel {
    private final RatingsRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public ConvenioViewModel(RatingsRepository repo) { this.repo = repo; }

    private final MutableLiveData<UiState<Void>> _rate = new MutableLiveData<>();
    public LiveData<UiState<Void>> rate = _rate;

    public void rateConvenio(int convenioId, String userId, int puntuacion){
        _rate.postValue(new UiState.Loading<>());
        io.execute(() -> {
            RatingsRepository.Result<Void> r = repo.rate(convenioId, userId, puntuacion);
            if (r.isSuccess()) _rate.postValue(new UiState.Success<>(null));
            else _rate.postValue(new UiState.Error<>(r.error));
        });
    }
}
