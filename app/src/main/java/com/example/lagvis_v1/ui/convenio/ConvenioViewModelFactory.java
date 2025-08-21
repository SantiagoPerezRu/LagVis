package com.example.lagvis_v1.ui.convenio;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.core.network.RetroFitProvider; // usa tu nombre real
import com.example.lagvis_v1.data.remote.RatingsApi;
import com.example.lagvis_v1.data.repository.RatingsRepositoryImpl;
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository;

public class ConvenioViewModelFactory implements ViewModelProvider.Factory {
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        RatingsApi api = RetroFitProvider.provideRatingsApi();
        RatingsRepository repo = new RatingsRepositoryImpl(api);
        return (T) new ConvenioViewModel(repo);
    }
}
