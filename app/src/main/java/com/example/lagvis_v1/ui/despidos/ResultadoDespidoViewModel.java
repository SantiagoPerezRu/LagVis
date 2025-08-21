package com.example.lagvis_v1.ui.despidos;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lagvis_v1.R;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class ResultadoDespidoViewModel extends AndroidViewModel {

    public static class Resultado {
        public final double improcedente, extIncumplimiento, extObjetiva,
                despidoColectivo, movilidadGeografica, modificacionCondiciones,
                victimasViolencia, extTemporal;

        public Resultado(double improcedente, double extIncumplimiento, double extObjetiva,
                         double despidoColectivo, double movilidadGeografica,
                         double modificacionCondiciones, double victimasViolencia,
                         double extTemporal) {
            this.improcedente = improcedente;
            this.extIncumplimiento = extIncumplimiento;
            this.extObjetiva = extObjetiva;
            this.despidoColectivo = despidoColectivo;
            this.movilidadGeografica = movilidadGeografica;
            this.modificacionCondiciones = modificacionCondiciones;
            this.victimasViolencia = victimasViolencia;
            this.extTemporal = extTemporal;
        }
    }

    public static class Ui {
        public final boolean loading;
        public final String error; // null si no hay
        public final Resultado resultado;

        public Ui(boolean loading, String error, Resultado resultado) {
            this.loading = loading; this.error = error; this.resultado = resultado;
        }
        public static Ui loading() { return new Ui(true, null, null); }
        public static Ui error(String e){ return new Ui(false, e, null); }
        public static Ui success(Resultado r){ return new Ui(false, null, r); }
    }

    private final MutableLiveData<Ui> state = new MutableLiveData<>();
    public LiveData<Ui> getState() { return state; }

    private final SingleLiveEvent<String> printHtmlEvent = new SingleLiveEvent<>();
    public LiveData<String> getPrintHtmlEvent() { return printHtmlEvent; }

    // Datos cacheados para regenerar HTML
    private double salarioDiario;
    private int mesesTrabajados;
    private long diasTrabajados;
    private String fechaInicioFormatted;
    private String fechaFinFormatted;

    public ResultadoDespidoViewModel(@NonNull Application app) { super(app); }

    // ===== API público =====

    public void cargarDatos(double salarioDiario, int mesesTrabajados, long diasTrabajados,
                            String fechaInicioFormatted, String fechaFinFormatted) {
        state.setValue(Ui.loading());
        this.salarioDiario = salarioDiario;
        this.mesesTrabajados = mesesTrabajados;
        this.diasTrabajados = diasTrabajados;
        this.fechaInicioFormatted = fechaInicioFormatted;
        this.fechaFinFormatted = fechaFinFormatted;

        Resultado r = new Resultado(
                calcularDespidoImprocedente(salarioDiario, mesesTrabajados),
                calcularExtincionPorIncumplimiento(salarioDiario, mesesTrabajados),
                calcularExtincionObjetiva(salarioDiario, mesesTrabajados),
                calcularDespidoColectivo(salarioDiario, mesesTrabajados),
                calcularMovilidadGeografica(salarioDiario, mesesTrabajados),
                calcularModificacionCondiciones(salarioDiario, mesesTrabajados),
                calcularVictimasViolencia(salarioDiario, mesesTrabajados),
                calcularExtincionTemporal(salarioDiario, (int) diasTrabajados)
        );
        state.setValue(Ui.success(r));
    }

    public void onExportarPdfClicked() {
        Ui ui = state.getValue();
        if (ui == null || ui.resultado == null) {
            state.setValue(Ui.error("No hay datos para exportar."));
            return;
        }
        printHtmlEvent.setValue(generarTablaHTML(ui.resultado));
    }

    // ===== Cálculos (idénticos a tu Activity) =====

    private double calcularDespidoImprocedente(double salarioDiario, int mesesTrabajados) {
        return salarioDiario * mesesTrabajados * 2.75;
    }
    private double calcularExtincionPorIncumplimiento(double salarioDiario, int mesesTrabajados) {
        return salarioDiario * mesesTrabajados * 2.75;
    }
    private double calcularExtincionObjetiva(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }
    private double calcularDespidoColectivo(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }
    private double calcularMovilidadGeografica(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }
    private double calcularModificacionCondiciones(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }
    private double calcularVictimasViolencia(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }
    private double calcularExtincionTemporal(double salarioDiario, int diasTrabajados) {
        return (salarioDiario * diasTrabajados * 12.0) / 365.0;
    }

    // ===== HTML y logo =====

    private String formatEuro(double v) {
        return String.format(Locale.getDefault(), "%.2f €", v);
    }

    private String generarTablaHTML(Resultado r) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><style>")
                .append("body{font-family:sans-serif;margin:20px;} ")
                .append("h1{text-align:center;color:#2C3E50;margin-top:110px;margin-bottom:25px;} ")
                .append("h2{text-align:center;color:#2C3E50;margin-top:20px;margin-bottom:15px;} ")
                .append("p{margin-bottom:5px;font-size:14px;color:#555;} ")
                .append("table{width:100%;border-collapse:collapse;margin-top:30px;} ")
                .append("th,td{border:1px solid #ddd;padding:12px;text-align:left;} ")
                .append("th{background:#f2f2f2;color:#333;font-weight:bold;} ")
                .append("td.value{font-weight:bold;color:#007BFF;} ")
                .append("p.note{margin-top:30px;font-size:14px;color:#666;text-align:center;border-top:1px solid #eee;padding-top:15px;} ")
                .append(".logo{position:absolute;top:0px;left:0px;width:150px;height:auto;} ")
                .append("</style></head><body>");

        String logoBase64Data = drawableToBase64(R.drawable.logotrans);
        htmlBuilder.append("<img class='logo' src='").append(logoBase64Data).append("' alt='Logo'>");
        htmlBuilder.append("<h1>Calculadora de Indemnizaciones de Despido LagVis</h1>");
        htmlBuilder.append("<h2>Detalles del Cálculo</h2>");
        htmlBuilder.append("<p><b>Fecha de inicio del contrato:</b> ")
                .append(fechaInicioFormatted != null ? fechaInicioFormatted : "N/A").append("</p>");
        htmlBuilder.append("<p><b>Fecha de fin del contrato:</b> ")
                .append(fechaFinFormatted != null ? fechaFinFormatted : "N/A").append("</p>");
        htmlBuilder.append("<p><b>Salario Diario:</b> ").append(formatEuro(salarioDiario)).append("</p>");
        htmlBuilder.append("<p><b>Meses trabajados (aproximados):</b> ").append(mesesTrabajados).append("</p>");
        htmlBuilder.append("<p><b>Días trabajados (totales):</b> ").append(diasTrabajados).append("</p>");

        htmlBuilder.append("<table>");
        htmlBuilder.append("<tr><th>Concepto</th><th>Importe</th></tr>");
        htmlBuilder.append("<tr><td>1. DESPIDO IMPROCEDENTE</td><td class='value'>").append(formatEuro(r.improcedente)).append("</td></tr>");
        htmlBuilder.append("<tr><td>2. EXTINCIÓN DEL CONTRATO POR VOLUNTAD DEL TRABAJADOR EN CASO DE INCUMPLIMIENTO GRAVE DEL EMPRESARIO</td><td class='value'>").append(formatEuro(r.extIncumplimiento)).append("</td></tr>");
        htmlBuilder.append("<tr><td>3. EXTINCIÓN POR CAUSAS OBJETIVAS PROCEDENTE Y TRABAJADOR INDEFINIDO NO FIJO</td><td class='value'>").append(formatEuro(r.extObjetiva)).append("</td></tr>");
        htmlBuilder.append("<tr><td>4. DESPIDO COLECTIVO PROCEDENTE</td><td class='value'>").append(formatEuro(r.despidoColectivo)).append("</td></tr>");
        htmlBuilder.append("<tr><td>5. MOVILIDAD GEOGRÁFICA</td><td class='value'>").append(formatEuro(r.movilidadGeografica)).append("</td></tr>");
        htmlBuilder.append("<tr><td>6. MODIFICACIÓN SUSTANCIAL DE CONDICIONES DE TRABAJO</td><td class='value'>").append(formatEuro(r.modificacionCondiciones)).append("</td></tr>");
        htmlBuilder.append("<tr><td>7. VÍCTIMAS DE VIOLENCIA DE GÉNERO, VIOLENCIA SEXUAL O TERRORISMO</td><td class='value'>").append(formatEuro(r.victimasViolencia)).append("</td></tr>");
        htmlBuilder.append("<tr><td>8. EXTINCIÓN DEL CONTRATO TEMPORAL - Contrato celebrado a partir del 1-1-2015</td><td class='value'>").append(formatEuro(r.extTemporal)).append("</td></tr>");
        htmlBuilder.append("</table>");
        htmlBuilder.append("<p class='note'><b>Nota importante:</b> Esto es solo una aproximación. En cualquier caso se recomienda consultar con un profesional.</p>");
        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }

    private String drawableToBase64(int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(getApplication(), drawableResId);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        }
        return "";
    }
}
