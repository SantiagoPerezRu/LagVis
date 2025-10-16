// file: app/src/main/java/com/example/lagvis_v1/ui/conveniosIa/ConveniosIaViewModel.kt
package com.example.lagvis_v1.ui.conveniosIa

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.data.repository.ia.GeminiConvenioRepository
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioUiModel // Modelo de datos compartido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class ConveniosIaViewModel(
    private val geminiRepo: GeminiConvenioRepository
) : ViewModel() {

    private val TAG = "ConveniosIaVM"

    private val _state: MutableStateFlow<UiState<ConvenioUiModel>> =
        MutableStateFlow(UiState.Initial())
    val state: StateFlow<UiState<ConvenioUiModel>> = _state

    private val _nav = MutableStateFlow<UiState<Boolean>>(UiState.Initial())
    val nav: StateFlow<UiState<Boolean>> = _nav

    fun consumeNav() {
        _nav.value = UiState.Initial()
    }

    fun resetState() {
        Log.d(TAG, "Reseteando estado del IA VM.")
        _state.value = UiState.Initial()
    }

    fun summarizeAndLoad(pdfUri: Uri) {
        // Bloquea ejecuciones múltiples si ya está cargando
        if (_state.value is UiState.Loading) return

        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                _state.value = UiState.Loading()
                _nav.value = UiState.Loading()
            }

            Log.d(TAG, "Iniciando resumen IA para Uri: $pdfUri")

            val result = runCatching {
                val xmlResult = geminiRepo.summarizePdfToXml(pdfUri)
                Log.d(TAG, "XML de Gemini recibido (longitud: ${xmlResult.length})")
                parseXmlToUiModel(xmlResult)
            }

            withContext(Dispatchers.Main) {
                result.onSuccess { uiModel ->
                    Log.i(TAG, "IA finalizada. UiModel generado con título: ${uiModel.titulo}")
                    _state.value = UiState.Success(uiModel)
                    _nav.value = UiState.Success(true)
                }.onFailure { e ->
                    val message = e.message ?: "Error desconocido al procesar el PDF con IA."
                    Log.e(TAG, "Error fatal al resumir con IA: $message", e)
                    _state.value = UiState.Error(message)
                    _nav.value = UiState.Error(message)
                }
            }
        }
    }

    /**
     * Lógica de parseo (COPIA ÍNTEGRA de la versión original)
     */
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
                    val path = stack.joinToString("/")

                    when (path) {
                        "convenio_colectivo/titulo" -> titulo = contenido
                        "convenio_colectivo/resumen_general" -> resumenGeneral = contenido

                        "convenio_colectivo/vacaciones/dias" -> diasVacaciones = contenido
                        "convenio_colectivo/vacaciones/observaciones" -> observacionesVacaciones = contenido

                        "convenio_colectivo/festivos/numero_dias" -> numeroFestivos = contenido
                        "convenio_colectivo/festivos/detalle" -> detalleFestivos = contenido

                        "convenio_colectivo/horas_extraordinarias/regulacion" -> regulacionHorasExtra = contenido

                        "convenio_colectivo/salario/salario_aproximado" ->
                            salarioAproximado = if (contenido.isNotEmpty()) "Salario Aproximado: $contenido" else ""
                        "convenio_colectivo/salario/informacion_importante" ->
                            salarioInfo = contenido

                        "convenio_colectivo/licencias/retribuidas/matrimonio" ->
                            licenciaMatrimonio = if (contenido.isNotEmpty()) "Matrimonio: $contenido" else ""
                        "convenio_colectivo/licencias/retribuidas/fallecimiento_familiares" ->
                            licenciaFallecimiento = if (contenido.isNotEmpty()) "Fallecimiento: $contenido" else ""
                        "convenio_colectivo/licencias/no_retribuidas/formacion" ->
                            licenciaFormacion = if (contenido.isNotEmpty()) "Formación: $contenido" else ""
                        "convenio_colectivo/licencias/no_retribuidas/otros" ->
                            licenciaOtros = if (contenido.isNotEmpty()) "Otros: $contenido" else ""

                        "convenio_colectivo/seguro/cobertura" -> coberturaSeguro = contenido
                        "convenio_colectivo/seguro/importe" -> importeSeguro = contenido

                        "convenio_colectivo/derechos_generales/igualdad" -> igualdad = contenido
                        "convenio_colectivo/derechos_generales/salud_laboral" -> saludLaboral = contenido
                        "convenio_colectivo/derechos_generales/conciliacion" -> conciliacion = contenido
                        "convenio_colectivo/derechos_generales/representacion" -> representacion = contenido

                        "convenio_colectivo/manutencion/detalleManun" -> detalleManutencion = contenido
                        "convenio_colectivo/manutencion/detalle" -> detalleManutencion = contenido
                    }

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

