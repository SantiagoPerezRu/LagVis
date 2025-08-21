// com/example/lagvis_v1/ui/auth/AdvancedFormViewModelFactory.java
package com.example.lagvis_v1.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.core.network.RetroFitProvider;
import com.example.lagvis_v1.data.remote.ProfileApi;
import com.example.lagvis_v1.data.repository.ProfileRepositoryImpl;
import com.example.lagvis_v1.dominio.repositorio.ProfileRepository;

public class AdvancedFormViewModelFactory implements ViewModelProvider.Factory {
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ProfileApi api = RetroFitProvider.provideProfileApi();
        ProfileRepository repo = new ProfileRepositoryImpl(api);
        return (T) new AdvancedFormViewModel(repo);
    }
}
