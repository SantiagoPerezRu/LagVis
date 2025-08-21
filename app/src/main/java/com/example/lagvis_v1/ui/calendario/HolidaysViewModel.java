package com.example.lagvis_v1.ui.calendario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.repositorio.HolidaysRepository;
import com.example.lagvis_v1.dominio.PublicHoliday;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HolidaysViewModel extends ViewModel {
    private final HolidaysRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public HolidaysViewModel(HolidaysRepository repo){ this.repo = repo; }

    private final MutableLiveData<UiState<List<PublicHoliday>>> _state = new MutableLiveData<>(new UiState.Loading<>());
    public LiveData<UiState<List<PublicHoliday>>> state = _state;

    public void load(int year, String countryCode){
        _state.postValue(new UiState.Loading<>());
        io.execute(() -> {
            HolidaysRepository.Result<List<PublicHoliday>> r = repo.getHolidays(year, countryCode);
            if (r.isSuccess()) _state.postValue(new UiState.Success<>(r.data));
            else _state.postValue(new UiState.Error<>(r.error));
        });
    }
}
