package com.example.lagvis_v1.ui.finiquitos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatosGeneralesFiniquitoViewModel extends ViewModel {

    public static class ResultadoFiniquito {
        public final double salarioPorDiasTrabajados; // salario del mes proporcional
        public final double importeVacaciones;        // vacaciones no disfrutadas
        public final double pagasExtra;               // prorrata (si 14 no prorr.)
        public final double totalFiniquito;           // subtotal sin indemnización
        public final double indemnizacion;            // según tipo + régimen 45/33
        public final double totalLiquidacion;         // finiquito + indemnización

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

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    static { SDF.setLenient(false); }

    private Date fechaInicio = null;
    private Date fechaFin    = null;

    private long diasTrabajados = 0;     // antigüedad total (para indemnización)
    private int diasMesTrabajado = 0;    // día del mes de baja (cap a 30)
    private int diasDevengoSemestre = 0; // días del semestre para prorrata extra (cap ~182)

    private final MutableLiveData<UiState<ResultadoFiniquito>> _state = new MutableLiveData<>();
    public LiveData<UiState<ResultadoFiniquito>> state = _state;

    private static long daysBetween(Date a, Date b) {
        long ms = Math.max(0L, b.getTime() - a.getTime());
        return TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS);
    }
    private static double round2(double x) { return Math.round(x * 100.0) / 100.0; }

    /** LLAMAR ANTES DE calcular(): fija antigüedad, días del mes y devengo de extras. */
    public void setFechasContrato(String fechaInicioStr, String fechaFinStr) {
        try {
            this.fechaInicio = SDF.parse(fechaInicioStr);
            this.fechaFin    = SDF.parse(fechaFinStr);
        } catch (ParseException e) {
            this.fechaInicio = null;
            this.fechaFin    = null;
        }
        if (fechaInicio == null || fechaFin == null || fechaFin.before(fechaInicio)) {
            diasTrabajados = 0;
            diasMesTrabajado = 0;
            diasDevengoSemestre = 0;
            return;
        }

        // Antigüedad total
        diasTrabajados = daysBetween(fechaInicio, fechaFin);

        // Días del mes trabajado (criterio nómina: base 30)
        Calendar cFin = Calendar.getInstance();
        cFin.setTime(fechaFin);
        int diaMes = cFin.get(Calendar.DAY_OF_MONTH);     // 1..31
        diasMesTrabajado = Math.max(0, Math.min(30, diaMes)); // 31 -> 30

        // Devengo semestre (enero–junio / julio–diciembre)
        Calendar inicioSem = Calendar.getInstance();
        inicioSem.setTime(fechaFin);
        int mes = inicioSem.get(Calendar.MONTH); // 0..11
        if (mes <= Calendar.JUNE) {
            inicioSem.set(Calendar.MONTH, Calendar.JANUARY);
            inicioSem.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            inicioSem.set(Calendar.MONTH, Calendar.JULY);
            inicioSem.set(Calendar.DAY_OF_MONTH, 1);
        }
        diasDevengoSemestre = (int) daysBetween(inicioSem.getTime(), fechaFin);
        if (diasDevengoSemestre > 182) diasDevengoSemestre = 182;
    }

    /** (Opcional) si quisieras forzar manualmente el día de mes. */
    public void setDiasMesTrabajado(int dias) { this.diasMesTrabajado = Math.max(0, Math.min(30, dias)); }

    /** Antigüedad manual (si llega de otra pantalla). No necesario si usas setFechasContrato. */
    public void setDiasTrabajados(long dias) { this.diasTrabajados = Math.max(0, dias); }

    /**
     * @param salarioAnual     Bruto anual (€).
     * @param diasVacaciones   Días de vacaciones pendientes.
     * @param posicionPagas    0=12 pagas, 1=14 (no prorrateadas), 2=prorrateadas.
     * @param tipoDespido      0=Disciplinario/procedente, 1=Objetivo, 2=Improcedente, 3=Nulo.
     */
    public void calcular(String salarioAnual, String diasVacaciones, int posicionPagas, int tipoDespido) {
        _state.postValue(new UiState.Loading<>());
        try {
            double salarioAnualD = Double.parseDouble(salarioAnual);
            int diasVac = Integer.parseInt(diasVacaciones);

            if (salarioAnualD <= 0 || diasVac < 0) {
                _state.postValue(new UiState.Error<>("Revisa salario anual y días de vacaciones."));
                return;
            }
            if (fechaInicio == null || fechaFin == null) {
                _state.postValue(new UiState.Error<>("Faltan fechas de inicio/fin."));
                return;
            }

            final double salarioMensualNomina = (posicionPagas == 1) ? (salarioAnualD / 14.0) : (salarioAnualD / 12.0);
            final double salarioDiario = salarioAnualD / 365.0;

            // (1) Salario del mes proporcional (base 30)
            int diasMes = Math.max(0, Math.min(30, this.diasMesTrabajado));
            double salarioPorDiasTrabajados = (salarioMensualNomina / 30.0) * diasMes;

            // (2) Vacaciones no disfrutadas
            double importeVacaciones = salarioDiario * Math.max(0, diasVac);

            // (3) Pagas extra (solo si 14 no prorrateadas)
            double pagasExtra = 0.0;
            if (posicionPagas == 1) {
                double unaPaga = salarioAnualD / 14.0;
                int diasDev = Math.max(0, Math.min(182, this.diasDevengoSemestre));
                pagasExtra = (unaPaga / 182.0) * diasDev;
            }

            // (4) Indemnización (incluye régimen 45/33 y topes)
            double indemnizacion = calcularIndemnizacion(tipoDespido, salarioDiario, fechaInicio, fechaFin);

            // (5) Resumen
            double totalFiniquito   = salarioPorDiasTrabajados + importeVacaciones + pagasExtra;
            double totalLiquidacion = totalFiniquito + indemnizacion;

            _state.postValue(new UiState.Success<>(
                    new ResultadoFiniquito(
                            round2(salarioPorDiasTrabajados),
                            round2(importeVacaciones),
                            round2(pagasExtra),
                            round2(totalFiniquito),
                            round2(indemnizacion),
                            round2(totalLiquidacion)
                    )
            ));
        } catch (NumberFormatException e) {
            _state.postValue(new UiState.Error<>("Error en el formato de datos."));
        }
    }

    // Objetivo: 20 d/año (tope 12 mensualidades/360 días).
    // Improcedente: 33 d/año (tope general 720 días); si hay tramo pre 12/02/2012, 45 d/año hasta esa fecha,
    // con tope especial: si lo devengado pre-2012 > 720, se usa ese mayor con límite 42 mensualidades (1260 días).
    private double calcularIndemnizacion(int tipoDespido,
                                         double salarioDiario,
                                         Date ini, Date fin) {

        if (tipoDespido == 1) { // Objetivo
            double anios = diasTrabajados / 365.0;
            double diasIndem = 20.0 * anios;
            double importe = salarioDiario * diasIndem;
            double topeImporte = salarioDiario * 360.0; // 12 * 30
            return Math.min(importe, topeImporte);
        }
        if (tipoDespido != 2) return 0.0; // Disciplinario/Nulo → 0

        Calendar cut = Calendar.getInstance();
        cut.set(2012, Calendar.FEBRUARY, 12, 0, 0, 0);
        Date fechaCorte = cut.getTime();

        long diasPre, diasPost;
        if (!fin.before(fechaCorte) && !ini.after(fechaCorte)) {
            Calendar preEnd = Calendar.getInstance();
            preEnd.set(2012, Calendar.FEBRUARY, 11, 23, 59, 59);
            diasPre  = daysBetween(ini, preEnd.getTime());
            diasPost = daysBetween(fechaCorte, fin);
        } else if (fin.before(fechaCorte)) {
            diasPre  = daysBetween(ini, fin);
            diasPost = 0;
        } else {
            diasPre  = 0;
            diasPost = daysBetween(ini, fin);
        }

        double diasIndemPre  = (diasPre  / 365.0) * 45.0;
        double diasIndemPost = (diasPost / 365.0) * 33.0;
        double diasIndemTot  = diasIndemPre + diasIndemPost;

        double topeDias = 720.0; // 24 mensualidades
        if (diasIndemPre > 720.0) {
            topeDias = Math.min(diasIndemPre, 42.0 * 30.0); // 42 mensualidades
        }
        diasIndemTot = Math.min(diasIndemTot, topeDias);
        return salarioDiario * diasIndemTot;
    }
}
