package com.example.lagvis_v1.ui.finiquitos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatosGeneralesFiniquitoViewModel extends ViewModel {

    public static class ResultadoFiniquito {
        public final double salarioPorDiasTrabajados;
        public final double importeVacaciones;
        public final double pagasExtra;
        public final double totalFiniquito;    // Subtotal finiquito (sin indemnización)
        public final double indemnizacion;     // Según tipo de despido
        public final double totalLiquidacion;  // Finiquito + indemnización

        public ResultadoFiniquito(double salarioPorDiasTrabajados,
                                  double importeVacaciones,
                                  double pagasExtra,
                                  double totalFiniquito,
                                  double indemnizacion,
                                  double totalLiquidacion) {
            this.salarioPorDiasTrabajados = salarioPorDiasTrabajados;
            this.importeVacaciones = importeVacaciones;
            this.pagasExtra = pagasExtra;
            this.totalFiniquito = totalFiniquito;
            this.indemnizacion = indemnizacion;
            this.totalLiquidacion = totalLiquidacion;
        }
    }

    // ===== Campos derivados de las fechas =====
    private long diasTrabajados = 0;     // antigüedad total para indemnización
    private int diasMesTrabajado = 0;    // días del mes de baja (0..30) para salario del mes
    private int diasDevengoSemestre = 0; // días devengados del semestre para pagas extra (0..182)

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    static { SDF.setLenient(false); }

    private final MutableLiveData<UiState<ResultadoFiniquito>> _state = new MutableLiveData<>();
    public LiveData<UiState<ResultadoFiniquito>> state = _state;

    // =================== Setters de contexto (fechas y ayudas) ===================

    /** Pásame las fechas (dd/MM/yyyy) y calculo antigüedad, días de mes y devengo de extras. */
    public void setFechasContrato(String fechaInicioStr, String fechaFinStr) {
        try {
            java.util.Date inicio = SDF.parse(fechaInicioStr);
            java.util.Date fin    = SDF.parse(fechaFinStr);
            if (inicio == null || fin == null) {
                this.diasTrabajados = 0;
                this.diasMesTrabajado = 0;
                this.diasDevengoSemestre = 0;
                return;
            }

            // 1) Antigüedad total (días)
            long diffMs = Math.max(0L, fin.getTime() - inicio.getTime());
            this.diasTrabajados = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);

            // 2) Días del mes trabajado (criterio nómina: base 30)
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(fin);
            int diaMes = calFin.get(Calendar.DAY_OF_MONTH); // 1..31
            this.diasMesTrabajado = Math.max(0, Math.min(30, diaMes)); // 31 => 30

            // 3) Días devengados del semestre para pagas extra (aprox. semestre natural)
            //    - Verano: 1 enero → 30 junio
            //    - Navidad: 1 julio → 31 diciembre
            Calendar inicioSem = Calendar.getInstance();
            inicioSem.setTime(fin);
            int mes = inicioSem.get(Calendar.MONTH); // 0=enero..11=diciembre
            if (mes <= Calendar.JUNE) {
                inicioSem.set(Calendar.MONTH, Calendar.JANUARY);
                inicioSem.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                inicioSem.set(Calendar.MONTH, Calendar.JULY);
                inicioSem.set(Calendar.DAY_OF_MONTH, 1);
            }
            long diffSemMs = Math.max(0L, fin.getTime() - inicioSem.getTimeInMillis());
            int diasSem = (int) TimeUnit.DAYS.convert(diffSemMs, TimeUnit.MILLISECONDS);
            this.diasDevengoSemestre = Math.max(0, Math.min(182, diasSem));
        } catch (Exception ignore) {
            this.diasTrabajados = 0;
            this.diasMesTrabajado = 0;
            this.diasDevengoSemestre = 0;
        }
    }

    /** Úsalo si prefieres fijar manualmente los días del mes de baja. */
    public void setDiasMesTrabajado(int dias) {
        this.diasMesTrabajado = Math.max(0, Math.min(30, dias));
    }

    /** Úsalo si prefieres fijar manualmente el devengo del semestre de pagas extra. */
    public void setDiasDevengoSemestre(int dias) {
        this.diasDevengoSemestre = Math.max(0, Math.min(186, dias));
    }

    /** Antigüedad total (en días) por si ya la calculas fuera y quieres fijarla manualmente. */
    public void setDiasTrabajados(long dias) {
        this.diasTrabajados = Math.max(0, dias);
    }

    // =============================== Cálculo principal ===============================

    /**
     * @param salarioAnual     Bruto anual (€). Si las pagas están prorrateadas, ya vendrá integrado.
     * @param diasVacaciones   Días de vacaciones pendientes.
     * @param posicionPagas    0 = 12 pagas, 1 = 14 pagas (NO prorrateadas), 2 = prorrateadas.
     * @param posicionTipoDesp 0 = Disciplinario/procedente, 1 = Objetivo, 2 = Improcedente, 3 = Nulo.
     */
    public void calcular(String salarioAnual, String diasVacaciones, int posicionPagas, int posicionTipoDesp) {
        _state.postValue(new UiState.Loading<>());

        try {
            double salarioAnualD = Double.parseDouble(salarioAnual);
            int diasVac = Integer.parseInt(diasVacaciones);

            if (salarioAnualD <= 0 || diasVac < 0) {
                _state.postValue(new UiState.Error<>("Revisa salario anual y días de vacaciones."));
                return;
            }

            // Base de cálculo
            final double salarioDiario = salarioAnualD / 365.0;
            // Mensual de nómina:
            // - 14 no prorrateadas => mensual ordinario = anual/14 (extras aparte)
            // - 12 o prorrateadas  => mensual = anual/12
            final double salarioMensualNomina = (posicionPagas == 1) ? (salarioAnualD / 14.0) : (salarioAnualD / 12.0);

            // (1) Salario por días del mes (base 30)
            final int diasMes = Math.max(0, Math.min(30, this.diasMesTrabajado));
            double salarioPorDiasTrabajados = (salarioMensualNomina / 30.0) * diasMes;

            // (2) Vacaciones no disfrutadas
            double importeVacaciones = salarioDiario * Math.max(0, diasVac);

            // (3) Pagas extra (solo si 14 NO prorrateadas): prorrata del semestre en curso
            double pagasExtra = 0.0;
            if (posicionPagas == 1) {
                double pagaExtraTipo = salarioAnualD / 14.0; // una paga
                int diasDev = Math.max(0, Math.min(182, this.diasDevengoSemestre));
                pagasExtra = (pagaExtraTipo / 182.0) * diasDev;
            }

            // (4) Indemnización (20/33 días/año con topes 12/24 mensualidades)
            double indemnizacion = 0.0;
            double aniosTrabajados = this.diasTrabajados / 365.0;

            switch (posicionTipoDesp) {
                case 1: { // Objetivo
                    double diasIndem = 20.0 * aniosTrabajados;
                    indemnizacion = salarioDiario * diasIndem;
                    double tope = salarioMensualNomina * 12.0;
                    indemnizacion = Math.min(indemnizacion, tope);
                    break;
                }
                case 2: { // Improcedente
                    double diasIndem = 33.0 * aniosTrabajados;
                    indemnizacion = salarioDiario * diasIndem;
                    double tope = salarioMensualNomina * 24.0;
                    indemnizacion = Math.min(indemnizacion, tope);
                    break;
                }
                case 3: // Nulo → readmisión/salarios de tramitación (habitualmente 0 en liquidación)
                case 0: // Disciplinario/procedente
                default:
                    indemnizacion = 0.0;
            }

            // (5) Subtotal finiquito (sin indemnización)
            double totalFiniquito = salarioPorDiasTrabajados + importeVacaciones + pagasExtra;

            // (6) Total a percibir
            double totalLiquidacion = totalFiniquito + indemnizacion;

            _state.postValue(new UiState.Success<>(
                    new ResultadoFiniquito(
                            redondear(salarioPorDiasTrabajados),
                            redondear(importeVacaciones),
                            redondear(pagasExtra),
                            redondear(totalFiniquito),
                            redondear(indemnizacion),
                            redondear(totalLiquidacion)
                    )
            ));
        } catch (NumberFormatException e) {
            _state.postValue(new UiState.Error<>("Error en el formato de datos."));
        }
    }

    private double redondear(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
