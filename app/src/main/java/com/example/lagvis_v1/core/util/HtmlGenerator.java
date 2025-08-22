package com.example.lagvis_v1.core.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.example.lagvis_v1.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HtmlGenerator {

    public HtmlGenerator(){}

    /*
    *
    * salarioMes    = getIntent().getDoubleExtra("salario", 0.0);
        vacaciones    = getIntent().getDoubleExtra("vacaciones", 0.0);
        pagasExtra    = getIntent().getDoubleExtra("pagasExtra", 0.0);
        finiquito     = getIntent().getDoubleExtra("finiquito", 0.0);
        indemnizacion = getIntent().getDoubleExtra("indemnizacion", 0.0);
        total         = finiquito + indemnizacion;
    *
    * */

    private String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%.2f €", amount);
    }

    public String drawableToBase64(Context context, int drawableResId) {
        Drawable drawable = context.getResources().getDrawable(drawableResId, null);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        }
        return "";
    }

    public String generarTablaHTMLFiniquito(double salarioMes, double vacaciones, double pagasExtra, double finiquito, double indemnizacion, double total, String logo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body{font-family:sans-serif;margin:40px;color:#333}")
                .append("h1{text-align:center;color:#1A237E;margin-bottom:30px;border-bottom:2px solid #EEE;padding-bottom:10px}")
                .append("table{width:100%;border-collapse:collapse;margin-top:20px}")
                .append("th,td{border:1px solid #E0E0E0;padding:12px 15px;text-align:left}")
                .append("th{background:#F5F5F5;color:#555;font-weight:bold;text-transform:uppercase;font-size:13px}")
                .append("td.amount{text-align:right;font-weight:bold}")
                .append(".total-row{background:#E0E0E0;font-weight:bold;font-size:16px}")
                .append(".total-amount{color:#D32F2F}")
                .append(".header{display:flex;align-items:center;justify-content:space-between;margin-bottom:10px}")
                .append(".logo{height:60px}")
                .append("</style></head><body>");


        html.append("<div class='header'>")
                .append("<img class='logo' src='").append(logo).append("'/>")
                .append("<div style='font-size:12px;color:#777'>Generado: ")
                .append(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()))
                .append("</div></div>");

        html.append("<h1>Resumen del cálculo de finiquito</h1>");

        html.append("<table>")
                .append("<tr><th>Concepto</th><th>Importe</th></tr>")
                .append("<tr><td>Salario del mes (proporcional 30 días)</td><td class='amount'>").append(formatAmount(salarioMes)).append("</td></tr>")
                .append("<tr><td>Vacaciones no disfrutadas</td><td class='amount'>").append(formatAmount(vacaciones)).append("</td></tr>")
                .append("<tr><td>Pagas extra (prorrata semestre)</td><td class='amount'>").append(formatAmount(pagasExtra)).append("</td></tr>")
                .append("<tr><td><b>Total finiquito (subtotal)</b></td><td class='amount'>").append(formatAmount(finiquito)).append("</td></tr>")
                .append("<tr><td>Indemnización por despido</td><td class='amount'>").append(formatAmount(indemnizacion)).append("</td></tr>")
                .append("<tr class='total-row'><td><b>TOTAL A PERCIBIR</b></td><td class='amount total-amount'>").append(formatAmount(total)).append("</td></tr>")
                .append("</table>");

        html.append("<p style='margin-top:20px;color:#777;font-size:12px'>")
                .append("Nota: Cálculo estimado según criterio habitual de nómina (base 30) y Estatuto de los Trabajadores.")
                .append("</p>");

        html.append("</body></html>");
        return html.toString();
    }

}
