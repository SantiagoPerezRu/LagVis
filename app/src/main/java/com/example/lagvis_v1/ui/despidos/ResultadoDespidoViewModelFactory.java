package com.example.lagvis_v1.ui.despidos;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ResultadoDespidoViewModelFactory implements ViewModelProvider.Factory {
    private final Application app;
    public ResultadoDespidoViewModelFactory(Application app){ this.app = app; }

    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResultadoDespidoViewModel(app);
    }
}
