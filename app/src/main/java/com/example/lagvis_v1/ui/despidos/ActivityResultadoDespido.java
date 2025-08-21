package com.example.lagvis_v1.ui.despidos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.databinding.ActivityResultadoDespidoBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import FiniquitosPackage.ActivityDatosGeneralesDespido;

public class ActivityResultadoDespido extends AppCompatActivity {

    private ActivityResultadoDespidoBinding binding;
    private WebView myWebView; // solo para imprimir
    private ResultadoDespidoViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultadoDespidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this, new ResultadoDespidoViewModelFactory(getApplication()))
                .get(ResultadoDespidoViewModel.class);

        // Observa estado para pintar resultados
        vm.getState().observe(this, ui -> {
            if (ui == null) return;

            binding.btnExportarPdf.setEnabled(!ui.loading);

            if (ui.error != null) {
                Toast.makeText(this, ui.error, Toast.LENGTH_LONG).show();
                return;
            }
            if (ui.resultado != null) {
                binding.tvResultadoDespidoImprocedente.setText(format(ui.resultado.improcedente));
                binding.tvResultadoExtincionIncumplimiento.setText(format(ui.resultado.extIncumplimiento));
                binding.tvResultadoExtincionObjetiva.setText(format(ui.resultado.extObjetiva));
                binding.tvResultadoDespidoColectivo.setText(format(ui.resultado.despidoColectivo));
                binding.tvResultadoMovilidadGeografica.setText(format(ui.resultado.movilidadGeografica));
                binding.tvResultadoModificacionCondiciones.setText(format(ui.resultado.modificacionCondiciones));
                binding.tvResultadoVictimasViolencia.setText(format(ui.resultado.victimasViolencia));
                binding.tvResultadoExtincionTemporal.setText(format(ui.resultado.extTemporal));
            }
        });

        // Observa evento one-shot para imprimir
        vm.getPrintHtmlEvent().observe(this, html -> {
            if (html == null) return;
            createPdfFromHtml(html);
        });

        // Carga datos del intent y delega al VM
        Intent intent = getIntent();
        if (intent != null) {
            double salarioDiario = intent.getDoubleExtra(ActivityDatosGeneralesDespido.EXTRA_SALARIO_DIARIO, 0.0);
            int mesesTrabajados  = intent.getIntExtra(ActivityDatosGeneralesDespido.EXTRA_MESES_TRABAJADOS, 0);
            long diasTrabajados  = intent.getLongExtra(ActivityDatosGeneralesDespido.EXTRA_DIAS_TRABAJADOS, 0);
            String fechaInicioFormatted = intent.getStringExtra("fechaInicioFormatted");
            String fechaFinFormatted    = intent.getStringExtra("fechaFinFormatted");

            vm.cargarDatos(salarioDiario, mesesTrabajados, diasTrabajados, fechaInicioFormatted, fechaFinFormatted);
        } else {
            Toast.makeText(this, "No se recibieron datos para calcular.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Click exportar (pide al VM el HTML)
        binding.btnExportarPdf.setOnClickListener(v -> vm.onExportarPdfClicked());
    }

    private String format(double v) {
        return String.format(Locale.getDefault(), "%.2f €", v);
    }

    private void createPdfFromHtml(String htmlDocument) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        myWebView = new WebView(this);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                String fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String jobName = "Calculo_Indemnizacion_por_despido_LagVis_" + fileNameDate;

                PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                PrintAttributes.Builder builder = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("res1", "Printer", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                printManager.print(jobName, printAdapter, builder.build());

                // liberar referencia (el WebView es temporal)
                myWebView.destroy();
                myWebView = null;
            }
        });

        myWebView.loadDataWithBaseURL(null, htmlDocument, "text/html", "UTF-8", null);
        Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (myWebView != null) {
            myWebView.destroy();
            myWebView = null;
        }
        super.onDestroy();
        binding = null; // liberar binding
    }
}
