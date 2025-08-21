package com.example.lagvis_v1.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.repositorio.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repo;

    public AuthViewModel(AuthRepository repo) { this.repo = repo; }

    // YA EXISTENTE (opcional)
    private final MutableLiveData<Boolean> _signedIn = new MutableLiveData<>();
    public LiveData<Boolean> signedIn = _signedIn;
    public void refresh(){ _signedIn.postValue(repo.isSignedIn()); }
    public String uidOrNull(){ return repo.uidOrNull(); }
    public void logout(){ repo.signOut(); refresh(); }

    private final MutableLiveData<UiState<Void>> _reset = new MutableLiveData<>();
    public LiveData<UiState<Void>> reset = _reset;
    public String currentEmailOrNull(){ return repo.currentEmail(); }


    private final MutableLiveData<UiState<Void>> _login = new MutableLiveData<>();
    public LiveData<UiState<Void>> login = _login;

    private final MutableLiveData<UiState<Void>> _signup = new MutableLiveData<>();
    public LiveData<UiState<Void>> signup = _signup;

    public void signIn(String email, String password){
        _login.postValue(new UiState.Loading<>());
        repo.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess() {
                _login.postValue(new UiState.Success<>(null));
                refresh();
            }
            @Override public void onError(String msg) {
                _login.postValue(new UiState.Error<>(msg != null ? msg : "Error de autenticación"));
            }
        });
    }

    public void resetPassword(String email){
        _reset.postValue(new UiState.Loading<>());
        repo.sendPasswordReset(email, new AuthRepository.AuthCallback() {
            @Override public void onSuccess() { _reset.postValue(new UiState.Success<>(null)); }
            @Override public void onError(String msg) { _reset.postValue(new UiState.Error<>(msg)); }
        });
    }

    public void signUp(String email, String password){
        _signup.postValue(new UiState.Loading<>());
        repo.signUp(email, password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess() {
                repo.sendEmailVerification(new AuthRepository.AuthCallback() {
                    @Override public void onSuccess() { _signup.postValue(new UiState.Success<>(null)); }
                    @Override public void onError(String msg) { _signup.postValue(new UiState.Error<>(msg)); }
                });
            }
            @Override public void onError(String msg) { _signup.postValue(new UiState.Error<>(msg)); }
        });
    }


    public void resetPasswordForCurrentUser(){
        String email = repo.currentEmail();
        if (email == null) {
            _reset.postValue(new UiState.Error<>("Sin email de usuario"));
            return;
        }
        resetPassword(email);
    }
}
