package com.example.lagvis_v1.ui.despidos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.lagvis_v1.core.ui.UiState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DatosGeneralesDespidoViewModel extends ViewModel {

    public static class Resultado {
        public final double salarioDiario;
        public final int mesesTrabajados;
        public final long diasTrabajados;
        public final String fechaInicioStr;
        public final String fechaFinStr;

        public Resultado(double salarioDiario, int mesesTrabajados, long diasTrabajados,
                         String fechaInicioStr, String fechaFinStr) {
            this.salarioDiario = salarioDiario;
            this.mesesTrabajados = mesesTrabajados;
            this.diasTrabajados = diasTrabajados;
            this.fechaInicioStr = fechaInicioStr;
            this.fechaFinStr = fechaFinStr;
        }
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final MutableLiveData<UiState<Resultado>> _state = new MutableLiveData<>();
    public LiveData<UiState<Resultado>> state = _state;

    public DatosGeneralesDespidoViewModel() { sdf.setLenient(false); }

    public void calcular(String fechaInicioStr, String fechaFinStr, String salarioDiarioStr) {
        _state.postValue(new UiState.Loading<>());

        if (isEmpty(fechaInicioStr) || isEmpty(fechaFinStr) || isEmpty(salarioDiarioStr)) {
            _state.postValue(new UiState.Error<>("Completa fechas y salario diario."));
            return;
        }
        try {
            Date inicio = sdf.parse(fechaInicioStr);
            Date fin    = sdf.parse(fechaFinStr);
            if (inicio == null || fin == null) {
                _state.postValue(new UiState.Error<>("Formato de fecha inválido."));
                return;
            }
            if (fin.before(inicio)) {
                _state.postValue(new UiState.Error<>("La fecha de fin no puede ser anterior a la de inicio."));
                return;
            }
            double salarioDiario = Double.parseDouble(salarioDiarioStr);
            if (salarioDiario <= 0) {
                _state.postValue(new UiState.Error<>("El salario diario debe ser mayor que 0."));
                return;
            }
            long diffMs = Math.abs(fin.getTime() - inicio.getTime());
            long diasTrabajados = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);
            int mesesTrabajados = (int) (diasTrabajados / 30);

            _state.postValue(new UiState.Success<>(
                    new Resultado(salarioDiario, mesesTrabajados, diasTrabajados, fechaInicioStr, fechaFinStr)
            ));
        } catch (ParseException | NumberFormatException e) {
            _state.postValue(new UiState.Error<>("Datos inválidos: " + e.getMessage()));
        }
    }

    private boolean isEmpty(String s){ return s == null || s.trim().isEmpty(); }
}
