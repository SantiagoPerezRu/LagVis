package com.example.lagvis_v1.ui.convenio;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ConvenioSelectorViewModelFactory implements ViewModelProvider.Factory {

    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ConvenioSelectorViewModel();
    }


}
