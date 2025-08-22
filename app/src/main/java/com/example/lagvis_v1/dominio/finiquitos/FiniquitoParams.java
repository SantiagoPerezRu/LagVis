package com.example.lagvis_v1.dominio.finiquitos;

public class FiniquitoParams {
    public final double salarioDia;              // € por día
    public final int diasTrabajadosMes;          // días del mes en curso
    public final int vacacionesPendientesDias;   // días pendientes
    public final double pagaExtraDevengada;      // € devengados de pagas extra (si las prorrateas, 0)
    public final double indemnizacionDiasPorAnio;// p.ej. 33, 20 (si no aplica, 0)
    public final double aniosAntiguedad;         // e.g. 2.5
    public final String causa;                   // "despido", "baja_voluntaria", "fin_contrato"
    public final double tipoIrpf;                // 0..1 (p.ej. 0.12) si quieres simular neto

    public FiniquitoParams(double salarioDia,
                           int diasTrabajadosMes,
                           int vacacionesPendientesDias,
                           double pagaExtraDevengada,
                           double indemnizacionDiasPorAnio,
                           double aniosAntiguedad,
                           String causa,
                           double tipoIrpf) {
        this.salarioDia = salarioDia;
        this.diasTrabajadosMes = diasTrabajadosMes;
        this.vacacionesPendientesDias = vacacionesPendientesDias;
        this.pagaExtraDevengada = pagaExtraDevengada;
        this.indemnizacionDiasPorAnio = indemnizacionDiasPorAnio;
        this.aniosAntiguedad = aniosAntiguedad;
        this.causa = causa;
        this.tipoIrpf = tipoIrpf;
    }
}
