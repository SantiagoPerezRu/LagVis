package FiniquitosPackage; // Asegúrate de que este sea tu paquete real

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Base64;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R; // Asegúrate de que este sea tu paquete de recursos real

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityResultadoFiniquito extends AppCompatActivity {

    private TextView tvVacaciones, tvPagasExtras, tvFiniquito, tvIndemnizacion, tvTotal;

    // Variables para almacenar los datos del finiquito a nivel de clase
    private double vacaciones;
    private double pagasExtras;
    private double finiquito;
    private double indemnizacion;
    private double total;

    // Botón para exportar el PDF
    private Button btnExportarPdfFiniquito;

    // WebView auxiliar para la generación de PDF (puede ser nulo si no está en uso)
    private WebView myWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_finiquito); // Asegúrate de que coincida con el nombre XML

        // 1. Referencias a los TextView
        tvVacaciones = findViewById(R.id.tvVacaciones);
        tvPagasExtras = findViewById(R.id.tvPagasExtras);
        tvFiniquito = findViewById(R.id.tvFiniquito);
        tvIndemnizacion = findViewById(R.id.tvIndemnizacion);
        tvTotal = findViewById(R.id.tvTotal);

        // Referencia al botón de exportar
        btnExportarPdfFiniquito = findViewById(R.id.btnExportarPdfFiniquito); // Asegúrate de que este ID exista en tu XML
        // Ahora el botón llama al método que usa WebView para generar el PDF
        btnExportarPdfFiniquito.setOnClickListener(v -> createPdfFromHtmlFiniquito());

        // 2. Obtener los datos del Intent y asignarlos a las variables de clase
        vacaciones = getIntent().getDoubleExtra("vacaciones", 0.0);
        pagasExtras = getIntent().getDoubleExtra("pagasExtras", 0.0);
        finiquito = getIntent().getDoubleExtra("finiquito", 0.0);
        indemnizacion = getIntent().getDoubleExtra("indemnizacion", 0.0);

        // 3. Mostrar cada valor con formato €
        tvVacaciones.setText(formatAmount(vacaciones));
        tvPagasExtras.setText(formatAmount(pagasExtras));
        tvFiniquito.setText(formatAmount(finiquito));
        tvIndemnizacion.setText(formatAmount(indemnizacion));

        // 4. Calcular total
        total = finiquito + indemnizacion;
        tvTotal.setText(formatAmount(total));
    }

    // Método auxiliar para formatear cantidades a €.
    private String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%.2f €", amount);
    }

    /**
     * Convierte un drawable a una cadena Base64 para incrustar en HTML.
     * @param drawableResId ID del recurso drawable (ej. R.drawable.logotrans).
     * @return Cadena Base64 del drawable o vacía si no es un BitmapDrawable.
     */
    private String drawableToBase64(int drawableResId) {
        Drawable drawable = getResources().getDrawable(drawableResId, null);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Comprime el bitmap a PNG (o JPEG si prefieres)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        }
        return ""; // Retorna vacío si no es un BitmapDrawable
    }

    /**
     * Genera el contenido HTML para el PDF del finiquito.
     * Incluye estilos CSS para una presentación mejorada.
     * @return String HTML con la tabla de resultados del finiquito.
     */
    private String generarTablaHTMLFiniquito() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: 'Roboto', sans-serif; margin: 40px; color: #333; }")
                .append("h1 { text-align: center; color: #1A237E; margin-bottom: 30px; font-size: 26px; border-bottom: 2px solid #EEE; padding-bottom: 10px; }")
                .append("h2 { color: #424242; margin-top: 30px; margin-bottom: 15px; font-size: 18px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }")
                .append("th, td { border: 1px solid #E0E0E0; padding: 12px 15px; text-align: left; }")
                .append("th { background-color: #F5F5F5; color: #555; font-weight: bold; text-transform: uppercase; font-size: 13px; }")
                .append("td.concept { font-weight: 500; color: #424242; }")
                .append("td.amount { text-align: right; font-weight: bold; color: #00796B; font-size: 15px; }") // Color teal para importes
                .append("tr:nth-child(even) { background-color: #F9F9F9; }") // Filas alternas
                .append("tr:hover { background-color: #F0F0F0; }") // Efecto hover (no visible en PDF, pero buena práctica)
                .append(".total-row { background-color: #E0E0E0; font-weight: bold; font-size: 18px; }")
                .append(".total-amount { color: #D32F2F; }") // Rojo para el total
                .append("p.note { margin-top: 40px; font-size: 13px; color: #666; text-align: center; border-top: 1px solid #EEE; padding-top: 20px; }")
                .append(".header-logo { float: left; margin-right: 20px; width: 100px; height: auto; }")
                .append(".header-date { float: right; font-size: 12px; color: #888; margin-top: 10px; }")
                .append(".clearfix::after { content: ''; clear: both; display: table; }") // Para limpiar floats
                .append("</style>")
                .append("</head>")
                .append("<body>");

        String logoBase64Data = drawableToBase64(R.drawable.logotrans);

        htmlBuilder.append("<div class='clearfix'>")
                .append("<img class='header-logo' src='").append(logoBase64Data).append("' alt='Logo'>")
                .append("<div class='header-date'>Fecha de generación: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date())).append("</div>")
                .append("</div>");

        htmlBuilder.append("<h1>Resumen del Cálculo de Finiquito</h1>");

        htmlBuilder.append("<h2>Detalles de Conceptos</h2>");
        htmlBuilder.append("<table>");
        htmlBuilder.append("<tr><th>Concepto</th><th>Importe</th></tr>");

        // Conceptos del Finiquito
        htmlBuilder.append("<tr><td class='concept'>Vacaciones no disfrutadas</td><td class='amount'>")
                .append(formatAmount(vacaciones))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td class='concept'>Pagas extras prorrateadas</td><td class='amount'>")
                .append(formatAmount(pagasExtras))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td class='concept'>Total Finiquito (Salario + Partes Proporcionales)</td><td class='amount'>")
                .append(formatAmount(finiquito))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td class='concept'>Indemnización por Despido</td><td class='amount'>")
                .append(formatAmount(indemnizacion))
                .append("</td></tr>");

        htmlBuilder.append("<tr class='total-row'><td class='concept'>TOTAL A PERCIBIR</td><td class='amount total-amount'>")
                .append(formatAmount(total))
                .append("</td></tr>");

        htmlBuilder.append("</table>");

        // Nota importante al final del PDF
        htmlBuilder.append("<p class='note'><b>Nota importante:</b> Esto es solo una aproximación. En cualquier caso se recomienda consultar con un profesional.</p>");

        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }

    /**
     * Inicia el proceso de creación de PDF a partir del HTML generado.
     */
    private void createPdfFromHtmlFiniquito() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Crear un WebView temporal. Es importante que no sea parte del layout visible.
        // Se crea aquí cada vez para asegurar un estado limpio y evitar problemas de renderizado.
        myWebView = new WebView(this);
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // Cuando la página HTML se ha cargado completamente en el WebView,
                // podemos iniciar el proceso de impresión.

                // Generar un nombre de archivo único para el PDF
                String appName = getString(R.string.app_name); // Obtener el nombre de la app de strings.xml
                String fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String jobName = "Calculo" + "_Finiquito_LagVis_" + fileNameDate;

                // Obtener el adaptador de impresión del WebView
                PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                // Configurar atributos de impresión (tamaño de página, orientación, calidad)
                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4); // Tamaño de papel A4
                builder.setResolution(new PrintAttributes.Resolution("res1", "Printer", 600, 600)); // 600 DPI
                builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS); // Márgenes mínimos (controlados por CSS)

                // Iniciar el diálogo de impresión/guardar PDF
                printManager.print(jobName, printAdapter, builder.build());

                // Liberar el WebView después de usarlo para evitar fugas de memoria
                // Es importante hacer esto después de que el trabajo de impresión ha comenzado.
                myWebView = null;
            }
        });

        // Generar el contenido HTML y cargarlo en el WebView
        String htmlDocument = generarTablaHTMLFiniquito();
        myWebView.loadDataWithBaseURL(null, htmlDocument, "text/html", "UTF-8", null);

        Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show();
    }
}