// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioVisualizer.kt
package com.example.lagvis_v1.ui.convenio

import android.os.Bundle
import android.util.Xml
import android.widget.Toast
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.util.BaseActivity
import com.example.lagvis_v1.databinding.ActivityConvenioBinding
import com.example.lagvis_v1.ui.auth.login.AuthViewModelFactoryKt
import com.example.lagvis_v1.ui.auth.login.AuthViewModelKt
import androidx.lifecycle.ViewModelProvider
import org.xmlpull.v1.XmlPullParser

class ConvenioVisualizer : BaseActivity() {

    private lateinit var binding: ActivityConvenioBinding

    private lateinit var vm: ConvenioViewModel
    private lateinit var authVm: AuthViewModelKt

    private var convenioIdActual: Int = -1
    private var usuarioIdActual: String? = null // uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvenioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // VMs
        vm = ViewModelProvider(this, ConvenioViewModelFactory())
            .get(ConvenioViewModel::class.java)
        authVm = ViewModelProvider(this, AuthViewModelFactoryKt())
            .get(AuthViewModelKt::class.java)

        convenioIdActual = intent.getIntExtra("sectorId", -1)
        usuarioIdActual = authVm.uidOrNull()

        cargarConvenioDesdeXML() // solo rellena UI con tu XML local

        // Observa envío de valoración
        vm.rate.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.btnEnviarValoracion.isEnabled = false
                is UiState.Success -> {
                    binding.btnEnviarValoracion.isEnabled = true
                    Toast.makeText(this, "Valoración enviada", Toast.LENGTH_LONG).show()
                }
                is UiState.Error -> {
                    binding.btnEnviarValoracion.isEnabled = true
                    val msg = state.message ?: "Error al enviar valoración"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        binding.btnEnviarValoracion.setOnClickListener {
            val rating = binding.ratingBar.rating
            if (rating <= 0f) {
                Toast.makeText(this, "Por favor, selecciona al menos una estrella.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val valoracionFinal = rating.toInt()

            if (convenioIdActual == -1 || usuarioIdActual == null) {
                Toast.makeText(this, "Error: usuario o convenio no válidos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            vm.rateConvenio(convenioIdActual, usuarioIdActual!!, valoracionFinal)
        }
    }

    private fun cargarConvenioDesdeXML() {
        try {
            val nombreArchivo = intent.getStringExtra("archivo_convenio") ?: return

            val resourceId = resources.getIdentifier(
                nombreArchivo.replace(".xml", ""),
                "raw",
                packageName
            )
            if (resourceId == 0) {
                showCustomToast("Archivo no encontrado!", getDrawable(R.drawable.ic_error_outline))
                return
            }

            resources.openRawResource(resourceId).use { inputStream ->
                val parser: XmlPullParser = Xml.newPullParser().apply {
                    setInput(inputStream, null)
                }

                var eventType = parser.eventType
                var tagActual: String? = null
                val texto = StringBuilder()

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tag = parser.name
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            tagActual = tag
                            texto.setLength(0)
                        }
                        XmlPullParser.TEXT -> {
                            texto.append(parser.text.trim())
                        }
                        XmlPullParser.END_TAG -> {
                            var contenido = texto.toString()
                            if (contenido.isEmpty()) contenido = ""
                            when (tag) {
                                "titulo" -> binding.tvTitulo.text = contenido
                                "resumen_general" -> binding.tvResumenGeneral.text = contenido
                                "dias" -> binding.tvDiasVacaciones.text = contenido
                                "observaciones" -> binding.tvObservacionesVacaciones.text = contenido
                                "numero_dias" -> binding.tvNumeroFestivos.text = contenido
                                "detalle" -> {
                                    if (tagActual == "detalle") {
                                        binding.tvDetalleFestivos.text = contenido
                                    } else if(tagActual == "detalleManun") {
                                        binding.tvDetalleManutencion.text = contenido
                                    }
                                }
                                "regulacion" -> binding.tvRegulacionHorasExtra.text = contenido
                                "salario" -> binding.tvSalarioInfo.text = contenido
                                "salario_aproximado" -> binding.tvSalario.text = "Salario Aproximado: $contenido"
                                "matrimonio" -> binding.tvLicenciaMatrimonio.text = "Matrimonio: $contenido"
                                "fallecimiento_familiares" -> binding.tvLicenciaFallecimiento.text = "Fallecimiento: $contenido"
                                "formacion" -> binding.tvLicenciaFormacion.text = "Formación: $contenido"
                                "otros" -> binding.tvLicenciaOtros.text = "Otros: $contenido"
                                "cobertura" -> binding.tvCoberturaSeguro.text = contenido
                                "importe" -> binding.tvImporteSeguro.text = contenido
                                "igualdad" -> binding.tvIgualdad.text = contenido
                                "salud_laboral" -> binding.tvSaludLaboral.text = contenido
                                "conciliacion" -> binding.tvConciliacion.text = contenido
                                "representacion" -> binding.tvRepresentacion.text = contenido
                                "manutencion" -> binding.tvDetalleManutencion.text = contenido
                            }
                        }
                    }
                    eventType = parser.next()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showCustomToast("Error al cargar el convenio!", getDrawable(R.drawable.ic_error_outline))
        }
    }
}
