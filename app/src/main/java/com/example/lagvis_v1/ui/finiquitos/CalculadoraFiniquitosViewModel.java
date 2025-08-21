package com.example.lagvis_v1.ui.finiquitos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.lagvis_v1.core.ui.UiState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalculadoraFiniquitosViewModel extends ViewModel {

    private static final String PATTERN = "dd/MM/yyyy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN, Locale.getDefault());

    private final MutableLiveData<UiState<Long>> _next = new MutableLiveData<>();
    public LiveData<UiState<Long>> next = _next;

    public CalculadoraFiniquitosViewModel() { sdf.setLenient(false); }

    public void onNextClicked(String fContrato, String fDespido) {
        if (isEmpty(fContrato) || isEmpty(fDespido)) {
            _next.postValue(new UiState.Error<>("Por favor, selecciona ambas fechas"));
            return;
        }
        try {
            Date d1 = sdf.parse(fContrato);
            Date d2 = sdf.parse(fDespido);
            if (d1 == null || d2 == null) {
                _next.postValue(new UiState.Error<>("Error al parsear las fechas."));
                return;
            }
            if (d2.before(d1)) {
                _next.postValue(new UiState.Error<>("La fecha de despido no puede ser anterior a la de contrato."));
                return;
            }
            long diff = d2.getTime() - d1.getTime();
            long dias = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            _next.postValue(new UiState.Success<>(dias));
        } catch (ParseException e) {
            _next.postValue(new UiState.Error<>("Error al parsear las fechas."));
        }
    }

    private boolean isEmpty(String s){ return s == null || s.trim().isEmpty(); }
}
