// com/example/lagvis_v1/ui/profile/ProfileViewModel.java
package com.example.lagvis_v1.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.model.UserProfile;
import com.example.lagvis_v1.dominio.repositorio.ProfileRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends ViewModel {
    private final ProfileRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public ProfileViewModel(ProfileRepository repo){ this.repo = repo; }

    private final MutableLiveData<UiState<UserProfile>> _state = new MutableLiveData<>(new UiState.Loading<>());
    public LiveData<UiState<UserProfile>> state = _state;

    public void load(String uid){
        _state.postValue(new UiState.Loading<>());
        io.execute(() -> {
            ProfileRepository.Result<UserProfile> r = repo.fetch(uid);
            if (r.isSuccess()) _state.postValue(new UiState.Success<>(r.data));
            else _state.postValue(new UiState.Error<>(r.error));
        });
    }
}
