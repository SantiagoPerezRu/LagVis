// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioSelectorViewModel.kt
package com.example.lagvis_v1.ui.convenio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.util.LagVisConstantes

class ConvenioSelectorViewModel : ViewModel() {

    data class NavData(
        val archivo: String,
        val comunidadId: Int,
        val sectorId: Int
    )

    private val sectoresEstatales: Set<String> = setOf(
        "Call Center", "Centros Enseñanza Privada", "Seguridad Privada",
        "Textil y Confección", "Perfumería", "Estaciones de Servicio"
    )

    private val _nav = MutableLiveData<UiState<NavData>>()
    val nav: LiveData<UiState<NavData>> = _nav

    fun onSiguiente(comunidad: String?, sector: String?) {
        val esEstatal = sector in sectoresEstatales

        if (sector.isNullOrBlank() || (!esEstatal && comunidad.isNullOrBlank())) {
            _nav.postValue(
                UiState.Error("Debes seleccionar un sector y, si no es estatal, una comunidad.")
            )
            return
        }

        val comunidadId = LagVisConstantes.getComunidadId(comunidad ?: "")
        val sectorId = LagVisConstantes.getSectorId(sector ?: "")

        val comunidadFinal = if (esEstatal) "estatal" else comunidad.orEmpty()
        val archivo = buildNombreArchivo(contenidoSafe(comunidadFinal), contenidoSafe(sector.orEmpty()))

        if (archivo == null) {
            _nav.postValue(UiState.Error("No se encontró un convenio para esta combinación."))
        } else {
            _nav.postValue(UiState.Success(NavData(archivo, comunidadId, sectorId)))
        }
    }

    fun consumeNav() {
        _nav.value = UiState.Loading()   // o un estado neutro que no dispare navegación
    }

    private fun contenidoSafe(s: String?): String = s?.trim().orEmpty()

    private fun buildNombreArchivo(comunidad: String, sector: String): String? {
        val comunidadSimplificada = simplificarNombreComunidad(comunidad)
        val nombreArchivo = (comunidadSimplificada + "_" + sector)
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace(" ", "_")

        // Igual que en Java: devolvemos el nombre; la existencia del recurso la valida el Fragment/Activity
        return "$nombreArchivo.xml"
    }

    private fun simplificarNombreComunidad(comunidad: String): String = when (comunidad) {
        "Comunidad de Madrid"   -> "madrid"
        "Comunidad Valenciana"  -> "valencia"
        "Illes Balears"         -> "baleares"
        "País Vasco"            -> "pais_vasco"
        "Andalucía"             -> "andalucia"
        "Aragón"                -> "aragon"
        "Asturias"              -> "asturias"
        "Cantabria"             -> "cantabria"
        "Castilla-La Mancha"    -> "castilla_la_mancha"
        "Castilla y León"       -> "castilla_y_leon"
        "Cataluña"              -> "cataluna"
        "Extremadura"           -> "extremadura"
        "Galicia"               -> "galicia"
        "Canarias"              -> "canarias"
        "La Rioja"              -> "la_rioja"
        "Región de Murcia"      -> "murcia"
        "Navarra"               -> "navarra"
        else -> comunidad.lowercase().replace(" ", "_")
    }
}
