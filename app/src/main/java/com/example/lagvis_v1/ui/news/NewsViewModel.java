package com.example.lagvis_v1.ui.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.NewsItem;
import com.example.lagvis_v1.dominio.repositorio.NewsRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NewsViewModel extends ViewModel {

    private final NewsRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public NewsViewModel(NewsRepository repo) {
        this.repo = repo;
    }

    private final MutableLiveData<UiState<List<NewsItem>>> _state = new MutableLiveData<>(new UiState.Loading<>());
    public LiveData<UiState<List<NewsItem>>> state = _state;


    private final MutableLiveData<UiState<Void>> _save = new MutableLiveData<>();
    public LiveData<UiState<Void>> save = _save;


    public void load(String query, String country, String category) {
        _state.postValue(new UiState.Loading<>());
        io.execute(() -> {
            NewsRepository.Result<List<NewsItem>> r = repo.fetchNews(query, country, category);
            if (r.isSuccess()) _state.postValue(new UiState.Success<>(r.data));
            else
                _state.postValue(new UiState.Error<>(r.error != null ? r.error : "Error desconocido"));
        });
    }

    public void save(String uid, NewsItem item){
        _save.postValue(new UiState.Loading<>());
        io.execute(() -> {
            NewsRepository.Result<Void> r = repo.saveNews(uid, item);
            if (r.isSuccess()) _save.postValue(new UiState.Success<>(null));
            else _save.postValue(new UiState.Error<>(r.error));
        });
    }
}

