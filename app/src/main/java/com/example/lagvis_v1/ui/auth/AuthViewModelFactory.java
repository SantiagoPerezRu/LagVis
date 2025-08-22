package com.example.lagvis_v1.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.data.repository.AuthRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModelFactory implements ViewModelProvider.Factory {
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AuthViewModel(new AuthRepositoryImpl(FirebaseAuth.getInstance()));
    }
}

