package com.example.lagvis_v1.ui.finiquitos;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DatosGeneralesFiniquitoViewModelFactory implements ViewModelProvider.Factory {
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DatosGeneralesFiniquitoViewModel();
    }
}
