// com/example/lagvis_v1/ui/auth/AdvancedFormViewModel.java
package com.example.lagvis_v1.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.repositorio.ProfileRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdvancedFormViewModel extends ViewModel {

    private final ProfileRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public AdvancedFormViewModel(ProfileRepository repo) {
        this.repo = repo;
    }

    private final MutableLiveData<UiState<Void>> _submit = new MutableLiveData<>();
    public LiveData<UiState<Void>> submit = _submit;

    public void send(String uid,
                     String nombre,
                     String apellido1,
                     String apellido2,
                     String comunidadId,
                     String sectorId,
                     String fechaNacimiento) {

        _submit.postValue(new UiState.Loading<>());
        io.execute(() -> {
            ProfileRepository.Result<Void> r = repo.insert(uid, nombre, apellido1, apellido2, comunidadId, sectorId, fechaNacimiento);
            if (r.isSuccess()) _submit.postValue(new UiState.Success<>(null));
            else _submit.postValue(new UiState.Error<>(r.error));
        });
    }
}
