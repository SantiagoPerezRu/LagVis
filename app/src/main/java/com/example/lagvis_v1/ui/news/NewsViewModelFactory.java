package com.example.lagvis_v1.ui.news;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.BuildConfig;

import com.example.lagvis_v1.core.network.RetroFitProvider;
import com.example.lagvis_v1.data.remote.NewsApi;
import com.example.lagvis_v1.data.remote.SaveNewsApi;
import com.example.lagvis_v1.dominio.repositorio.NewsRepository;
import com.example.lagvis_v1.data.repository.NewsRepositoryImpl;

public class NewsViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        NewsApi api = RetroFitProvider.provideNewsApi();
        SaveNewsApi saveApi = RetroFitProvider.provideSaveNewsApi();

        NewsRepository repo = new NewsRepositoryImpl(api, BuildConfig.API_KEY_NEWS, saveApi); // pasas la key aquí
        return (T) new NewsViewModel(repo);
    }
}