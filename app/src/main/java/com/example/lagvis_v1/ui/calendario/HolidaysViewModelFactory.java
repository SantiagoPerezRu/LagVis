// com/example/lagvis_v1/ui/calendario/HolidaysViewModelFactory.java
package com.example.lagvis_v1.ui.calendario;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.core.network.RetroFitProvider;
import com.example.lagvis_v1.data.remote.HolidaysApi;
import com.example.lagvis_v1.dominio.repositorio.HolidaysRepository;
import com.example.lagvis_v1.data.repository.HolidaysRepositoryImpl;

public class HolidaysViewModelFactory implements ViewModelProvider.Factory {
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        HolidaysApi api = RetroFitProvider.provideHolidaysApi();
        HolidaysRepository repo = new HolidaysRepositoryImpl(api);
        return (T) new HolidaysViewModel(repo);
    }
}
