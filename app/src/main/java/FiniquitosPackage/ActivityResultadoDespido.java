package FiniquitosPackage; // Asegúrate de que este sea tu paquete real

import android.content.Context; // Necesario para getSystemService
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.print.PrintAttributes; // Para configurar el PDF
import android.print.PrintDocumentAdapter; // Para el adaptador de impresión
import android.print.PrintManager; // Para el servicio de impresión
import android.util.Base64;
import android.webkit.WebView; // Necesario para renderizar HTML
import android.webkit.WebViewClient; // Para controlar la carga del WebView
import android.widget.Button; // Para el botón de exportar
import android.widget.TextView;
import android.widget.Toast; // Para mostrar mensajes al usuario
import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R; // Asegúrate de que este sea tu paquete de recursos real

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date; // Necesario para la fecha en el nombre del archivo PDF
import java.util.Locale;

public class ActivityResultadoDespido extends AppCompatActivity {

    // TextViews para mostrar los resultados de cada tipo de indemnización
    private TextView tvResultadoDespidoImprocedente;
    private TextView tvResultadoExtincionIncumplimiento;
    private TextView tvResultadoExtincionObjetiva;
    private TextView tvResultadoDespidoColectivo;
    private TextView tvResultadoMovilidadGeografica;
    private TextView tvResultadoModificacionCondiciones;
    private TextView tvResultadoVictimasViolencia;
    private TextView tvResultadoExtincionTemporal;

    // Variables para almacenar los datos recibidos del Intent
    private double salarioDiario;
    private int mesesTrabajados;
    private long diasTrabajados;
    private String fechaInicioFormatted; // Variable para almacenar la fecha de inicio formateada
    private String fechaFinFormatted;    // Variable para almacenar la fecha de fin formateada

    // Nuevo botón para exportar
    private Button btnExportarPdf;

    // WebView auxiliar para la generación de PDF (puede ser nulo si no está en uso)
    private WebView myWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_despido); // Vincula con el nuevo layout de resultados

        // Inicialización de los TextViews de resultados
        tvResultadoDespidoImprocedente = findViewById(R.id.tvResultadoDespidoImprocedente);
        tvResultadoExtincionIncumplimiento = findViewById(R.id.tvResultadoExtincionIncumplimiento);
        tvResultadoExtincionObjetiva = findViewById(R.id.tvResultadoExtincionObjetiva);
        tvResultadoDespidoColectivo = findViewById(R.id.tvResultadoDespidoColectivo);
        tvResultadoMovilidadGeografica = findViewById(R.id.tvResultadoMovilidadGeografica);
        tvResultadoModificacionCondiciones = findViewById(R.id.tvResultadoModificacionCondiciones);
        tvResultadoVictimasViolencia = findViewById(R.id.tvResultadoVictimasViolencia);
        tvResultadoExtincionTemporal = findViewById(R.id.tvResultadoExtincionTemporal);

        // Inicializar el nuevo botón
        btnExportarPdf = findViewById(R.id.btnExportarPdf);
        btnExportarPdf.setOnClickListener(v -> createPdfFromHtml()); // Llama a la función de exportación

        // Obtener los datos pasados desde ActivityDatosGeneralesDespido
        Intent intent = getIntent();
        if (intent != null) {
            salarioDiario = intent.getDoubleExtra(ActivityDatosGeneralesDespido.EXTRA_SALARIO_DIARIO, 0.0);
            mesesTrabajados = intent.getIntExtra(ActivityDatosGeneralesDespido.EXTRA_MESES_TRABAJADOS, 0);
            diasTrabajados = intent.getLongExtra(ActivityDatosGeneralesDespido.EXTRA_DIAS_TRABAJADOS, 0);
            // Obtener las fechas formateadas del Intent (necesario si quieres mostrarlas en el PDF)
            fechaInicioFormatted = intent.getStringExtra("fechaInicioFormatted");
            fechaFinFormatted = intent.getStringExtra("fechaFinFormatted");

            // Realizar cálculos y mostrar resultados en la UI de la app
            mostrarResultados();
        } else {
            Toast.makeText(this, "No se recibieron datos para calcular.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no hay datos
        }
    }

    /**
     * Realiza los cálculos de indemnización y actualiza los TextViews en la UI.
     */
    private void mostrarResultados() {
        tvResultadoDespidoImprocedente.setText(String.format(Locale.getDefault(), "%.2f €", calcularDespidoImprocedente(salarioDiario, mesesTrabajados)));
        tvResultadoExtincionIncumplimiento.setText(String.format(Locale.getDefault(), "%.2f €", calcularExtincionPorIncumplimiento(salarioDiario, mesesTrabajados)));
        tvResultadoExtincionObjetiva.setText(String.format(Locale.getDefault(), "%.2f €", calcularExtincionObjetiva(salarioDiario, mesesTrabajados)));
        tvResultadoDespidoColectivo.setText(String.format(Locale.getDefault(), "%.2f €", calcularDespidoColectivo(salarioDiario, mesesTrabajados)));
        tvResultadoMovilidadGeografica.setText(String.format(Locale.getDefault(), "%.2f €", calcularMovilidadGeografica(salarioDiario, mesesTrabajados)));
        tvResultadoModificacionCondiciones.setText(String.format(Locale.getDefault(), "%.2f €", calcularModificacionCondiciones(salarioDiario, mesesTrabajados)));
        tvResultadoVictimasViolencia.setText(String.format(Locale.getDefault(), "%.2f €", calcularVictimasViolencia(salarioDiario, mesesTrabajados)));
        tvResultadoExtincionTemporal.setText(String.format(Locale.getDefault(), "%.2f €", calcularExtincionTemporal(salarioDiario, (int) diasTrabajados)));
    }

    // --- Funciones de Cálculo de Indemnizaciones (mantengo las mismas, sin cambios) ---

    public double calcularDespidoImprocedente(double salarioDiario, int mesesTrabajados) {
        return salarioDiario * mesesTrabajados * 2.75;
    }

    public double calcularExtincionPorIncumplimiento(double salarioDiario, int mesesTrabajados) {
        return salarioDiario * mesesTrabajados * 2.75;
    }

    public double calcularExtincionObjetiva(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }

    public double calcularDespidoColectivo(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }

    public double calcularMovilidadGeografica(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }

    public double calcularModificacionCondiciones(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }

    public double calcularVictimasViolencia(double salarioDiario, int mesesTrabajados) {
        return (salarioDiario * mesesTrabajados * 20.0) / 12.0;
    }

    public double calcularExtincionTemporal(double salarioDiario, int diasTrabajados) {
        return (salarioDiario * diasTrabajados * 12.0) / 365.0;
    }

    /**
     * Genera el contenido HTML que representa la tabla de resultados.
     * @return String HTML con la tabla de resultados.
     */
    private String generarTablaHTML() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: sans-serif; margin: 20px; }")
                .append("h1 { text-align: center; color: #2C3E50; margin-top:110px; margin-bottom: 25px;  }")
                .append("h2 { text-align: center; color: #2C3E50; margin-top: 20px; margin-bottom: 15px; }")
                .append("p { margin-bottom: 5px; font-size: 14px; color: #555; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 30px; }")
                .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }")
                .append("th { background-color: #f2f2f2; color: #333; font-weight: bold; }")
                .append("td.value { font-weight: bold; color: #007BFF; }")
                .append("p.note { margin-top: 30px; font-size: 14px; color: #666; text-align: center; border-top: 1px solid #eee; padding-top: 15px; }")
                .append(".logo { position: absolute; top: 0px; left: 0px; width: 150px; height: auto; }")
                .append("</style>")
                .append("</head>")
                .append("<body>");

        String logoBase64Data = drawableToBase64(R.drawable.logotrans);


        htmlBuilder.append("<img class='logo' src='").append(logoBase64Data).append("' alt='Logo'>");
        htmlBuilder.append("<h1>Calculadora de Indemnizaciones de Despido LagVis</h1>");
        htmlBuilder.append("<h2>Detalles del Cálculo</h2>");
        htmlBuilder.append("<p><b>Fecha de inicio del contrato:</b> ")
                .append(fechaInicioFormatted != null ? fechaInicioFormatted : "N/A")
                .append("</p>");
        htmlBuilder.append("<p><b>Fecha de fin del contrato:</b> ")
                .append(fechaFinFormatted != null ? fechaFinFormatted : "N/A")
                .append("</p>");
        htmlBuilder.append("<p><b>Salario Diario:</b> ")
                .append(String.format(Locale.getDefault(), "%.2f €", salarioDiario))
                .append("</p>");
        htmlBuilder.append("<p><b>Meses trabajados (aproximados):</b> ")
                .append(mesesTrabajados)
                .append("</p>");
        htmlBuilder.append("<p><b>Días trabajados (totales):</b> ")
                .append(diasTrabajados)
                .append("</p>");


        htmlBuilder.append("<table>");
        htmlBuilder.append("<tr><th>Concepto</th><th>Importe</th></tr>");

        // Añadir cada fila de la tabla con los resultados
        htmlBuilder.append("<tr><td>1. DESPIDO IMPROCEDENTE</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularDespidoImprocedente(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>2. EXTINCIÓN DEL CONTRATO POR VOLUNTAD DEL TRABAJADOR EN CASO DE INCUMPLIMIENTO GRAVE DEL EMPRESARIO</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularExtincionPorIncumplimiento(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>3. EXTINCIÓN POR CAUSAS OBJETIVAS PROCEDENTE Y TRABAJADOR INDEFINIDO NO FIJO</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularExtincionObjetiva(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>4. DESPIDO COLECTIVO PROCEDENTE</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularDespidoColectivo(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>5. MOVILIDAD GEOGRÁFICA</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularMovilidadGeografica(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>6. MODIFICACIÓN SUSTANCIAL DE CONDICIONES DE TRABAJO</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularModificacionCondiciones(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>7. VÍCTIMAS DE VIOLENCIA DE GÉNERO, VIOLENCIA SEXUAL O TERRORISMO</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularVictimasViolencia(salarioDiario, mesesTrabajados)))
                .append("</td></tr>");
        htmlBuilder.append("<tr><td>8. EXTINCIÓN DEL CONTRATO TEMPORAL - Contrato celebrado a partir del 1-1-2015</td><td class='value'>")
                .append(String.format(Locale.getDefault(), "%.2f €", calcularExtincionTemporal(salarioDiario, (int) diasTrabajados)))
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
    private void createPdfFromHtml() {
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
                String jobName = "Calculo" + "_Indemnizacion_por_despido_LagVis_" + fileNameDate;

                // Obtener el adaptador de impresión del WebView
                PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                // Configurar atributos de impresión (tamaño de página, orientación, calidad)
                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4); // Tamaño de papel A4
                builder.setResolution(new PrintAttributes.Resolution("res1", "Printer", 600, 600)); // 600 DPI
                builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS); // Margenes mínimos

                // Iniciar el diálogo de impresión/guardar PDF
                printManager.print(jobName, printAdapter, builder.build());

                // Liberar el WebView después de usarlo para evitar fugas de memoria
                // Es importante hacer esto después de que el trabajo de impresión ha comenzado.
                myWebView = null;
            }
        });

        // Generar el contenido HTML y cargarlo en el WebView
        String htmlDocument = generarTablaHTML();
        myWebView.loadDataWithBaseURL(null, htmlDocument, "text/html", "UTF-8", null);

        Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show();
    }

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


}