package com.example.lagvis_v1.ui.finiquitos;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.util.HtmlGenerator;
import com.example.lagvis_v1.databinding.ActivityResultadoFiniquitoBinding;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityResultadoFiniquito extends AppCompatActivity {

    private ActivityResultadoFiniquitoBinding binding;

    private double salarioMes;
    private double vacaciones;
    private double pagasExtra;
    private double finiquito;
    private double indemnizacion;
    private double total;

    private WebView myWebView;

    private HtmlGenerator htmlGenerator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        htmlGenerator = new HtmlGenerator();

        super.onCreate(savedInstanceState);
        binding = ActivityResultadoFiniquitoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        salarioMes    = getIntent().getDoubleExtra("salario", 0.0);
        vacaciones    = getIntent().getDoubleExtra("vacaciones", 0.0);
        pagasExtra    = getIntent().getDoubleExtra("pagasExtra", 0.0);
        finiquito     = getIntent().getDoubleExtra("finiquito", 0.0);
        indemnizacion = getIntent().getDoubleExtra("indemnizacion", 0.0);
        total         = finiquito + indemnizacion;

        binding.tvSalarioMes.setText(formatAmount(salarioMes));
        binding.tvVacaciones.setText(formatAmount(vacaciones));
        binding.tvPagasExtras.setText(formatAmount(pagasExtra));
        binding.tvFiniquito.setText(formatAmount(finiquito));
        binding.tvIndemnizacion.setText(formatAmount(indemnizacion));
        binding.tvTotal.setText(formatAmount(total));

        if (binding.btnInfoFiniquito != null) {
            binding.btnInfoFiniquito.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("¿Qué es el finiquito?")
                            .setMessage("El finiquito es la suma de las cantidades pendientes al finalizar el contrato:\n\n" +
                                    "• Salario de los días trabajados del mes\n" +
                                    "• Vacaciones devengadas y no disfrutadas\n" +
                                    "• Parte proporcional de pagas extra (si no están prorrateadas)\n\n" +
                                    "No incluye la indemnización por despido, que se muestra por separado.")
                            .setPositiveButton("Entendido", null)
                            .show()
            );
        }

        binding.btnExportarPdfFiniquito.setOnClickListener(v -> createPdfFromHtmlFiniquito());
    }

   private String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%.2f €", amount);
    }

    private void createPdfFromHtmlFiniquito() {
        PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        myWebView = new WebView(this);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                String jobName = "Calculo_Finiquito_LagVis_" +
                        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                PrintDocumentAdapter adapter = view.createPrintDocumentAdapter(jobName);

                PrintAttributes.Builder b = new PrintAttributes.Builder();
                b.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                b.setResolution(new PrintAttributes.Resolution("res1","Printer",600,600));
                b.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                pm.print(jobName, adapter, b.build());
                myWebView = null;
            }
        });

        String logo = htmlGenerator.drawableToBase64(this, R.drawable.logotrans);
        String htmlFinal = htmlGenerator.generarTablaHTMLFiniquito(salarioMes, vacaciones, pagasExtra, finiquito, indemnizacion, total, logo);



        myWebView.loadDataWithBaseURL(null, htmlFinal, "text/html", "UTF-8", null);
        Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show();
    }
}
