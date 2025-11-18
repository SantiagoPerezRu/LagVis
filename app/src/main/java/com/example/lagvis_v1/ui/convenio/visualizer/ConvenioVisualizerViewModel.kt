// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioVisualizerViewModel.kt
package com.example.lagvis_v1.ui.convenio.visualizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.repositorio.convenio.ConvenioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class ConvenioVisualizerViewModel(
    private val repo: ConvenioRepository
) : ViewModel() {
    companion object {
        private const val TAG = "ConvenioVM"
    }

        // 👇 tipo explícito en ambos lados
        private val _state: MutableStateFlow<UiState<ConvenioUiModel>> =
            MutableStateFlow(UiState.Loading<ConvenioUiModel>())
        val state: StateFlow<UiState<ConvenioUiModel>> = _state

    fun load(archivo: String) {
        // 👉 Lanzamos la corrutina en IO (no en Main)
        viewModelScope.launch(Dispatchers.IO) {
            // Publicar estado en Main (opcional pero limpio)
            withContext(Dispatchers.Main) {
                _state.value = UiState.Loading<ConvenioUiModel>()
            }

            val result = runCatching {
                // 🔹 TODO esto ocurre en IO:
                repo.ensureCached(archivo)          // descarga si falta
                val xml = repo.readLocalXml(archivo) // lee local
                parseXmlToUiModel(xml)               // parse en memoria (también IO)
            }

            // Volvemos a Main para publicar estado
            withContext(Dispatchers.Main) {
                result.onSuccess { ui ->
                    _state.value = UiState.Success(ui)
                }.onFailure { e ->
                    _state.value = UiState.Error<ConvenioUiModel>(
                        e.message ?: "Error al cargar convenio"
                    )
                }
            }
        }
    }

    private fun parseXmlToUiModel(xml: String): ConvenioUiModel {
        val parser = XmlPullParserFactory.newInstance()
            .apply { isNamespaceAware = false }
            .newPullParser().also { it.setInput(StringReader(xml)) }

        val stack = ArrayDeque<String>()
        val text = StringBuilder()

        var titulo = ""
        var resumenGeneral = ""
        var diasVacaciones = ""
        var observacionesVacaciones = ""
        var numeroFestivos = ""
        var detalleFestivos = ""
        var regulacionHorasExtra = ""
        var salarioInfo = ""
        var salarioAproximado = ""
        var licenciaMatrimonio = ""
        var licenciaFallecimiento = ""
        var licenciaFormacion = ""
        var licenciaOtros = ""
        var coberturaSeguro = ""
        var importeSeguro = ""
        var igualdad = ""
        var saludLaboral = ""
        var conciliacion = ""
        var representacion = ""
        var detalleManutencion = ""

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    stack.addLast(parser.name)
                    text.setLength(0)
                }
                XmlPullParser.TEXT -> {
                    text.append(parser.text ?: "")
                }
                XmlPullParser.END_TAG -> {
                    val contenido = text.toString().trim()
                    val path = stack.joinToString("/") // ej: convenio_colectivo/manutencion/detalleManun

                    when (path) {
                        "convenio_colectivo/titulo" -> titulo = contenido
                        "convenio_colectivo/resumen_general" -> resumenGeneral = contenido

                        // Vacaciones
                        "convenio_colectivo/vacaciones/dias" -> diasVacaciones = contenido
                        "convenio_colectivo/vacaciones/observaciones" -> observacionesVacaciones = contenido

                        // Festivos
                        "convenio_colectivo/festivos/numero_dias" -> numeroFestivos = contenido
                        "convenio_colectivo/festivos/detalle" -> detalleFestivos = contenido

                        // Horas extra
                        "convenio_colectivo/horas_extraordinarias/regulacion" -> regulacionHorasExtra = contenido

                        // Salario
                        "convenio_colectivo/salario/salario_aproximado" ->
                            salarioAproximado = if (contenido.isNotEmpty()) "Salario Aproximado: $contenido" else ""
                        "convenio_colectivo/salario/informacion_importante" ->
                            salarioInfo = contenido

                        // Licencias (retribuidas / no retribuidas)
                        "convenio_colectivo/licencias/retribuidas/matrimonio" ->
                            licenciaMatrimonio = if (contenido.isNotEmpty()) "Matrimonio: $contenido" else ""
                        "convenio_colectivo/licencias/retribuidas/fallecimiento_familiares" ->
                            licenciaFallecimiento = if (contenido.isNotEmpty()) "Fallecimiento: $contenido" else ""
                        "convenio_colectivo/licencias/no_retribuidas/formacion" ->
                            licenciaFormacion = if (contenido.isNotEmpty()) "Formación: $contenido" else ""
                        "convenio_colectivo/licencias/no_retribuidas/otros" ->
                            licenciaOtros = if (contenido.isNotEmpty()) "Otros: $contenido" else ""

                        // Seguro
                        "convenio_colectivo/seguro/cobertura" -> coberturaSeguro = contenido
                        "convenio_colectivo/seguro/importe" -> importeSeguro = contenido

                        // Derechos generales
                        "convenio_colectivo/derechos_generales/igualdad" -> igualdad = contenido
                        "convenio_colectivo/derechos_generales/salud_laboral" -> saludLaboral = contenido
                        "convenio_colectivo/derechos_generales/conciliacion" -> conciliacion = contenido
                        "convenio_colectivo/derechos_generales/representacion" -> representacion = contenido
                        // (hay un <formacion> dentro de derechos_generales que no mapeamos: ignorable)

                        // 🔸 Manutención NUEVO
                        "convenio_colectivo/manutencion/detalleManun" -> detalleManutencion = contenido

                        // Compatibilidad con formatos antiguos:
                        "convenio_colectivo/manutencion/detalle" -> detalleManutencion = contenido
                    }

                    // salir del tag actual
                    stack.removeLast()
                    text.setLength(0)
                }
            }
            event = parser.next()
        }

        return ConvenioUiModel(
            titulo = titulo,
            resumenGeneral = resumenGeneral,
            diasVacaciones = diasVacaciones,
            observacionesVacaciones = observacionesVacaciones,
            numeroFestivos = numeroFestivos,
            detalleFestivos = detalleFestivos,
            regulacionHorasExtra = regulacionHorasExtra,
            salarioInfo = salarioInfo,
            salarioAproximado = salarioAproximado,
            licenciaMatrimonio = licenciaMatrimonio,
            licenciaFallecimiento = licenciaFallecimiento,
            licenciaFormacion = licenciaFormacion,
            licenciaOtros = licenciaOtros,
            coberturaSeguro = coberturaSeguro,
            importeSeguro = importeSeguro,
            igualdad = igualdad,
            saludLaboral = saludLaboral,
            conciliacion = conciliacion,
            representacion = representacion,
            detalleManutencion = detalleManutencion
        )
    }

    }

