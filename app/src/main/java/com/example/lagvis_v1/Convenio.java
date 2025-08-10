package com.example.lagvis_v1;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

// Imports necesarios (asegúrate de que estén todos)
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Convenio extends BaseActivity {

    private TextView tvTitulo, tvResumenGeneral, tvDiasVacaciones, tvObservacionesVacaciones, tvNumeroFestivos, tvDetalleFestivos, tvRegulacionHorasExtra, tvLicenciaMatrimonio, tvLicenciaFallecimiento, tvLicenciaFormacion, tvLicenciaOtros, tvCoberturaSeguro, tvImporteSeguro, tvDetalleManutencion, tvIgualdad, tvFormacion, tvSaludLaboral, tvConciliacion, tvRepresentacion, tvSalario, tvSalarioInfo;
    private Button btnEnviarValoracion;
    private RatingBar ratingBar;

    // Variables para guardar los datos importantes
    private FirebaseAuth auth;
    private int convenioIdActual = -1;
    private String usuarioIdActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenio);

        // Inicializamos vistas y Firebase Auth
        inicializarVistas();
        auth = FirebaseAuth.getInstance();

        convenioIdActual = getIntent().getIntExtra("sectorId", -1);


        if (auth.getCurrentUser() != null) {
            usuarioIdActual = auth.getCurrentUser().getUid();
        }

        cargarConvenioDesdeXML();


        btnEnviarValoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos la puntuación del RatingBar
                float valoracionFloat = ratingBar.getRating();
                if (valoracionFloat == 0) {
                    Toast.makeText(Convenio.this, "Por favor, selecciona al menos una estrella.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int valoracionFinal = (int) valoracionFloat;

                // Verificamos que tenemos los IDs antes de enviar
                if (convenioIdActual != -1 && usuarioIdActual != null) {
                    enviarValoracion(convenioIdActual, usuarioIdActual, valoracionFinal);
                } else {
                    Toast.makeText(Convenio.this, "Error: No se pudo identificar el usuario o el convenio.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tvTitulo);
        tvResumenGeneral = findViewById(R.id.tvResumenGeneral);
        tvDiasVacaciones = findViewById(R.id.tvDiasVacaciones);
        tvObservacionesVacaciones = findViewById(R.id.tvObservacionesVacaciones);
        tvNumeroFestivos = findViewById(R.id.tvNumeroFestivos);
        tvDetalleFestivos = findViewById(R.id.tvDetalleFestivos);
        tvRegulacionHorasExtra = findViewById(R.id.tvRegulacionHorasExtra);
        tvLicenciaMatrimonio = findViewById(R.id.tvLicenciaMatrimonio);
        tvLicenciaFallecimiento = findViewById(R.id.tvLicenciaFallecimiento);
        tvLicenciaFormacion = findViewById(R.id.tvLicenciaFormacion);
        tvLicenciaOtros = findViewById(R.id.tvLicenciaOtros);
        tvCoberturaSeguro = findViewById(R.id.tvCoberturaSeguro);
        tvImporteSeguro = findViewById(R.id.tvImporteSeguro);
        tvDetalleManutencion = findViewById(R.id.tvDetalleManutencion);
        tvIgualdad = findViewById(R.id.tvIgualdad);
        tvFormacion = findViewById(R.id.tvFormacion);
        tvSaludLaboral = findViewById(R.id.tvSaludLaboral);
        tvConciliacion = findViewById(R.id.tvConciliacion);
        tvRepresentacion = findViewById(R.id.tvRepresentacion);
        tvSalario = findViewById(R.id.tvSalario);
        tvSalarioInfo = findViewById(R.id.tvSalarioInfo);
        btnEnviarValoracion = findViewById(R.id.btnEnviarValoracion);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void cargarConvenioDesdeXML() {
        try {
            String nombreArchivo = getIntent().getStringExtra("archivo_convenio");
            if (nombreArchivo == null || nombreArchivo.isEmpty()) return;
            int resourceId = getResources().getIdentifier(nombreArchivo.replace(".xml", ""), "raw", getPackageName());
            if (resourceId == 0) {
                showCustomToast("Archivo no encontrado!", getDrawable(R.drawable.ic_error_outline));
                return;
            }
            InputStream inputStream = getResources().openRawResource(resourceId);
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
                        switch (tag) {
                            case "titulo": tvTitulo.setText(contenido); break;
                            case "resumen_general": tvResumenGeneral.setText(contenido); break;
                            case "dias": tvDiasVacaciones.setText(contenido); break;
                            case "observaciones": tvObservacionesVacaciones.setText(contenido); break;
                            case "numero_dias": tvNumeroFestivos.setText(contenido); break;
                            case "detalle": if ("detalle".equals(tagActual)) { tvDetalleFestivos.setText(contenido); } else { tvDetalleManutencion.setText(contenido); } break;
                            case "regulacion": tvRegulacionHorasExtra.setText(contenido); break;
                            case "salario": tvSalarioInfo.setText(contenido); break;
                            case "salario_aproximado": tvSalario.setText("Salario Aproximado: " + contenido); break;
                            case "matrimonio": tvLicenciaMatrimonio.setText("Matrimonio: " + contenido); break;
                            case "fallecimiento_familiares": tvLicenciaFallecimiento.setText("Fallecimiento: " + contenido); break;
                            case "formacion": tvLicenciaFormacion.setText("Formación: " + contenido); break;
                            case "otros": tvLicenciaOtros.setText("Otros: " + contenido); break;
                            case "cobertura": tvCoberturaSeguro.setText(contenido); break;
                            case "importe": tvImporteSeguro.setText(contenido); break;
                            case "igualdad": tvIgualdad.setText(contenido); break;
                            case "salud_laboral": tvSaludLaboral.setText(contenido); break;
                            case "conciliacion": tvConciliacion.setText(contenido); break;
                            case "representacion": tvRepresentacion.setText(contenido); break;
                            case "manutencion": tvDetalleManutencion.setText(contenido); break;
                        }
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            showCustomToast("Error al cargar el convenio!", getDrawable(R.drawable.ic_error_outline));
        }
    }


    private void enviarValoracion(final int convenioId, final String usuarioId, final int puntuacion) {
        String url = LagVisConstantes.ENDPOINT_INSERTAR_VALORACION;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(Convenio.this, response, Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(Convenio.this, "Error de conexión: " + error.toString(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("convenio_id", String.valueOf(convenioId));
                params.put("usuario_id", usuarioId);
                params.put("puntuacion", String.valueOf(puntuacion));
                return params;
            }
        };
        queue.add(stringRequest);
    }
}