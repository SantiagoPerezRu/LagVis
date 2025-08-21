package com.example.lagvis_v1.ui.convenio;

import android.os.Bundle;
import android.util.Xml;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseActivity;
import com.example.lagvis_v1.databinding.ActivityConvenioBinding;
import com.example.lagvis_v1.ui.auth.AuthViewModel;
import com.example.lagvis_v1.ui.auth.AuthViewModelFactory;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class ConvenioVisualizer extends BaseActivity {

    private ActivityConvenioBinding binding;

    private ConvenioViewModel vm;
    private AuthViewModel authVm;

    private int convenioIdActual = -1;
    private String usuarioIdActual; // uid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConvenioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // VMs
        vm = new ViewModelProvider(this, new ConvenioViewModelFactory())
                .get(ConvenioViewModel.class);
        authVm = new ViewModelProvider(this, new AuthViewModelFactory())
                .get(AuthViewModel.class);

        convenioIdActual = getIntent().getIntExtra("sectorId", -1);
        usuarioIdActual = authVm.uidOrNull();

        cargarConvenioDesdeXML(); // solo rellena UI con tu XML local

        // Observa envío de valoración
        vm.rate.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.btnEnviarValoracion.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.btnEnviarValoracion.setEnabled(true);
                Toast.makeText(this, "Valoración enviada", Toast.LENGTH_LONG).show();
            } else if (state instanceof UiState.Error) {
                binding.btnEnviarValoracion.setEnabled(true);
                String msg = ((UiState.Error<?>) state).message;
                Toast.makeText(this, msg != null ? msg : "Error al enviar valoración", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnEnviarValoracion.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();
            if (rating <= 0f) {
                Toast.makeText(this, "Por favor, selecciona al menos una estrella.", Toast.LENGTH_SHORT).show();
                return;
            }
            int valoracionFinal = (int) rating;

            if (convenioIdActual == -1 || usuarioIdActual == null) {
                Toast.makeText(this, "Error: usuario o convenio no válidos.", Toast.LENGTH_LONG).show();
                return;
            }

            vm.rateConvenio(convenioIdActual, usuarioIdActual, valoracionFinal);
        });
    }

    private void cargarConvenioDesdeXML() {
        try {
            String nombreArchivo = getIntent().getStringExtra("archivo_convenio");
            if (nombreArchivo == null || nombreArchivo.isEmpty()) return;

            int resourceId = getResources().getIdentifier(
                    nombreArchivo.replace(".xml", ""), "raw", getPackageName());
            if (resourceId == 0) {
                showCustomToast("Archivo no encontrado!", getDrawable(R.drawable.ic_error_outline));
                return;
            }

            try (InputStream inputStream = getResources().openRawResource(resourceId)) {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(inputStream, null);
                int eventType = parser.getEventType();
                String tagActual = null;
                StringBuilder texto = new StringBuilder();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tag = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            tagActual = tag;
                            texto.setLength(0);
                            break;
                        case XmlPullParser.TEXT:
                            texto.append(parser.getText().trim());
                            break;
                        case XmlPullParser.END_TAG:
                            String contenido = texto.toString();
                            if (contenido == null) contenido = "";
                            switch (tag) {
                                case "titulo": binding.tvTitulo.setText(contenido); break;
                                case "resumen_general": binding.tvResumenGeneral.setText(contenido); break;
                                case "dias": binding.tvDiasVacaciones.setText(contenido); break;
                                case "observaciones": binding.tvObservacionesVacaciones.setText(contenido); break;
                                case "numero_dias": binding.tvNumeroFestivos.setText(contenido); break;
                                case "detalle":
                                    if ("detalle".equals(tagActual)) {
                                        binding.tvDetalleFestivos.setText(contenido);
                                    } else {
                                        binding.tvDetalleManutencion.setText(contenido);
                                    }
                                    break;
                                case "regulacion": binding.tvRegulacionHorasExtra.setText(contenido); break;
                                case "salario": binding.tvSalarioInfo.setText(contenido); break;
                                case "salario_aproximado": binding.tvSalario.setText("Salario Aproximado: " + contenido); break;
                                case "matrimonio": binding.tvLicenciaMatrimonio.setText("Matrimonio: " + contenido); break;
                                case "fallecimiento_familiares": binding.tvLicenciaFallecimiento.setText("Fallecimiento: " + contenido); break;
                                case "formacion": binding.tvLicenciaFormacion.setText("Formación: " + contenido); break;
                                case "otros": binding.tvLicenciaOtros.setText("Otros: " + contenido); break;
                                case "cobertura": binding.tvCoberturaSeguro.setText(contenido); break;
                                case "importe": binding.tvImporteSeguro.setText(contenido); break;
                                case "igualdad": binding.tvIgualdad.setText(contenido); break;
                                case "salud_laboral": binding.tvSaludLaboral.setText(contenido); break;
                                case "conciliacion": binding.tvConciliacion.setText(contenido); break;
                                case "representacion": binding.tvRepresentacion.setText(contenido); break;
                                case "manutencion": binding.tvDetalleManutencion.setText(contenido); break;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showCustomToast("Error al cargar el convenio!", getDrawable(R.drawable.ic_error_outline));
        }
    }
}
