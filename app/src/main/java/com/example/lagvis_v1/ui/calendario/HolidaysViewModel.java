// com/example/lagvis_v1/ui/calendario/HolidaysViewModel.java
package com.example.lagvis_v1.ui.calendario;

import androidx.lifecycle.*;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.dominio.repositorio.HolidaysRepository;
import com.example.lagvis_v1.dominio.PublicHoliday;
import java.util.List;
import java.util.concurrent.*;

public class HolidaysViewModel extends ViewModel {
    private final HolidaysRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public HolidaysViewModel(HolidaysRepository repo){ this.repo = repo; }

    private final MutableLiveData<UiState<List<PublicHoliday>>> _state =
            new MutableLiveData<>(new UiState.Loading<>());
    public LiveData<UiState<List<PublicHoliday>>> state = _state;

    // NUEVA FIRMA:
    public void loadByProvince(int year, String provinciaSlug){
        _state.postValue(new UiState.Loading<>());
        io.execute(() -> {
            var r = repo.getHolidaysByProvince(year, provinciaSlug);
            if (r.isSuccess()) _state.postValue(new UiState.Success<>(r.data));
            else _state.postValue(new UiState.Error<>(r.error));
        });
    }
}
