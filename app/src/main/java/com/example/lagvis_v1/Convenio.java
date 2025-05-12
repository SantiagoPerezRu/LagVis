package com.example.lagvis_v1;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class Convenio extends BaseActivity {

    private TextView tvTitulo, tvResumenGeneral, tvDiasVacaciones, tvObservacionesVacaciones,
            tvNumeroFestivos, tvDetalleFestivos, tvRegulacionHorasExtra,
            tvLicenciaMatrimonio, tvLicenciaFallecimiento, tvLicenciaFormacion, tvLicenciaOtros,
            tvCoberturaSeguro, tvImporteSeguro, tvDetalleManutencion,
            tvIgualdad, tvFormacion, tvSaludLaboral, tvConciliacion, tvRepresentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenio);

        inicializarVistas();
        cargarConvenioDesdeXML();
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
    }

    private void cargarConvenioDesdeXML() {
        try {
            // 1. Obtener el nombre del archivo desde el Intent
            String nombreArchivo = getIntent().getStringExtra("archivo_convenio");
           // Toast.makeText(this, "Primer nombre: " + nombreArchivo, Toast.LENGTH_SHORT).show();

            if (nombreArchivo == null || nombreArchivo.isEmpty()) {
              //  Toast.makeText(this, "Nombre de archivo no proporcionado", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Obtener el ID del recurso raw dinámicamente
            int resourceId = getResources().getIdentifier(nombreArchivo.replace(".xml", ""), "raw", getPackageName());


            if (resourceId == 0) {
                //Toast.makeText(this, "Archivo no encontrado: " + nombreArchivo, Toast.LENGTH_SHORT).show();
                Drawable checkIcon = getDrawable(R.drawable.ic_error_outline);
                showCustomToast("Archivo no encontrado!", checkIcon);

                return;
            }

            // 3. Cargar y parsear el XML
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
                            case "titulo":
                                tvTitulo.setText(contenido);
                                break;
                            case "resumen_general":
                                tvResumenGeneral.setText(contenido);
                                break;
                            case "dias":
                                tvDiasVacaciones.setText(contenido);
                                break;
                            case "observaciones":
                                tvObservacionesVacaciones.setText(contenido);
                                break;
                            case "numero_dias":
                                tvNumeroFestivos.setText(contenido);
                                break;
                            case "detalle":
                                if ("detalle".equals(tagActual)) {
                                    tvDetalleFestivos.setText(contenido);
                                } else {
                                    tvDetalleManutencion.setText(contenido);
                                }
                                break;
                            case "regulacion":
                                tvRegulacionHorasExtra.setText(contenido);
                                break;
                            case "matrimonio":
                                tvLicenciaMatrimonio.setText("Matrimonio: " + contenido);
                                break;
                            case "fallecimiento_familiares":
                                tvLicenciaFallecimiento.setText("Fallecimiento: " + contenido);
                                break;
                            case "formacion":
                                tvLicenciaFormacion.setText("Formación: " + contenido);
                                break;
                            case "otros":
                                tvLicenciaOtros.setText("Otros: " + contenido);
                                break;
                            case "cobertura":
                                tvCoberturaSeguro.setText(contenido);
                                break;
                            case "importe":
                                tvImporteSeguro.setText(contenido);
                                break;
                            case "igualdad":
                                tvIgualdad.setText(contenido);
                                break;
                            case "salud_laboral":
                                tvSaludLaboral.setText(contenido);
                                break;
                            case "conciliacion":
                                tvConciliacion.setText(contenido);
                                break;
                            case "representacion":
                                tvRepresentacion.setText(contenido);
                                break;
                            case "manutencion":
                                tvDetalleManutencion.setText(contenido);
                                break;
                        }
                        break;
                }

                eventType = parser.next();
            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error al cargar el convenio", Toast.LENGTH_SHORT).show();
            Drawable checkIcon = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("Error al cargar el convenio!", checkIcon);

        }
    }

}
