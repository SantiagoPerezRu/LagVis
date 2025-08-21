package com.example.lagvis_v1.dominio.finiquitos;

public class CalcularFiniquitoUseCase {

    public FiniquitoResult execute(FiniquitoParams p) {
        double salariosDev = redondear(p.salarioDia * p.diasTrabajadosMes);
        double vacNoDisf   = redondear(p.salarioDia * p.vacacionesPendientesDias);
        double extras      = redondear(Math.max(0, p.pagaExtraDevengada));

        double indemn      = 0.0;
        if ("despido".equalsIgnoreCase(p.causa) || "fin_contrato".equalsIgnoreCase(p.causa)) {
            // Indemnización simple: salario día * (días por año) * años
            indemn = redondear(p.salarioDia * p.indemnizacionDiasPorAnio * p.aniosAntiguedad);
        }

        double totalBruto  = redondear(salariosDev + vacNoDisf + extras + indemn);
        double retencion   = redondear(totalBruto * Math.max(0, Math.min(p.tipoIrpf, 1)));
        double totalNeto   = redondear(totalBruto - retencion);

        return new FiniquitoResult(salariosDev, vacNoDisf, extras, indemn, totalBruto, retencion, totalNeto);
    }

    private double redondear(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
