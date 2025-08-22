package com.example.lagvis_v1.dominio.finiquitos;

public class FiniquitoResult {
    public final double salariosDevengados;
    public final double vacacionesNoDisfrutadas;
    public final double pagaExtraDevengada;
    public final double indemnizacion;    // 0 si no aplica
    public final double totalBruto;
    public final double retencionIrpf;
    public final double totalNeto;

    public FiniquitoResult(double salariosDevengados, double vacacionesNoDisfrutadas,
                           double pagaExtraDevengada, double indemnizacion,
                           double totalBruto, double retencionIrpf, double totalNeto) {
        this.salariosDevengados = salariosDevengados;
        this.vacacionesNoDisfrutadas = vacacionesNoDisfrutadas;
        this.pagaExtraDevengada = pagaExtraDevengada;
        this.indemnizacion = indemnizacion;
        this.totalBruto = totalBruto;
        this.retencionIrpf = retencionIrpf;
        this.totalNeto = totalNeto;
    }
}
