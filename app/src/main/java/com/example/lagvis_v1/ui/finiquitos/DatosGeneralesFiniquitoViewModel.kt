// DatosGeneralesFiniquitoViewModel.kt
package com.example.lagvis_v1.ui.finiquitos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lagvis_v1.core.ui.UiState
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class DatosGeneralesFiniquitoViewModel : ViewModel() {

    data class Resultado(
        val salarioPorDiasTrabajados: Double,
        val importeVacaciones: Double,
        val pagasExtra: Double,
        val totalFiniquito: Double,
        val indemnizacion: Double,
        val totalLiquidacion: Double
    )

    private val _state = MutableLiveData<UiState<Resultado>>(UiState.Error("Introduce datos y pulsa Calcular"))
    val state: LiveData<UiState<Resultado>> = _state

    // Evento one-shot para que la UI “navegue” a la pantalla de resultados
    private val _openResults = MutableLiveData<Resultado?>()
    val openResults: LiveData<Resultado?> = _openResults
    fun consumeOpenResults() { _openResults.value = null }

    private var fechaInicio: Date? = null
    private var fechaFin: Date? = null
    private var diasTrabajados: Long = 0
    private var diasMesTrabajado: Int = 0
    private var diasDevengoSemestre: Int = 0

    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }

    fun setFechasContrato(inicio: String, fin: String) {
        try {
            fechaInicio = sdf.parse(inicio)
            fechaFin = sdf.parse(fin)
        } catch (_: Exception) {
            fechaInicio = null; fechaFin = null
        }
        val ini = fechaInicio; val f = fechaFin
        if (ini == null || f == null || f.before(ini)) {
            diasTrabajados = 0; diasMesTrabajado = 0; diasDevengoSemestre = 0; return
        }
        diasTrabajados = daysBetween(ini, f)
        val cFin = Calendar.getInstance().apply { time = f }
        diasMesTrabajado = cFin.get(Calendar.DAY_OF_MONTH).coerceIn(0, 30)
        val inicioSem = Calendar.getInstance().apply { time = f }
        if (inicioSem.get(Calendar.MONTH) <= Calendar.JUNE) {
            inicioSem.set(Calendar.MONTH, Calendar.JANUARY); inicioSem.set(Calendar.DAY_OF_MONTH, 1)
        } else {
            inicioSem.set(Calendar.MONTH, Calendar.JULY); inicioSem.set(Calendar.DAY_OF_MONTH, 1)
        }
        diasDevengoSemestre = daysBetween(inicioSem.time, f).toInt().coerceAtMost(182)
    }

    fun calcular(salarioAnual: String, diasVacaciones: String, posicionPagas: Int, tipoDespido: Int) {
        _state.postValue(UiState.Loading())
        try {
            val anual = salarioAnual.toDouble()
            val vac = diasVacaciones.toInt()
            val ini = fechaInicio; val fin = fechaFin
            if (anual <= 0 || vac < 0) return _state.postValue(UiState.Error("Revisa salario y vacaciones."))
            if (ini == null || fin == null) return _state.postValue(UiState.Error("Faltan fechas."))

            val salarioMensual = if (posicionPagas == 1) anual / 14.0 else anual / 12.0
            val salarioDiario  = anual / 365.0

            val salarioMesProp = (salarioMensual / 30.0) * diasMesTrabajado
            val importeVac     = salarioDiario * vac.coerceAtLeast(0)
            val pagasExtra     = if (posicionPagas == 1) (anual / 14.0) / 182.0 * diasDevengoSemestre else 0.0
            val indemnizacion  = calcularIndemnizacion(tipoDespido, salarioDiario, ini, fin)

            val totalFiniquito   = salarioMesProp + importeVac + pagasExtra
            val totalLiquidacion = totalFiniquito + indemnizacion

            val r = Resultado(
                round2(salarioMesProp), round2(importeVac), round2(pagasExtra),
                round2(totalFiniquito), round2(indemnizacion), round2(totalLiquidacion)
            )
            _state.postValue(UiState.Success(r))
            _openResults.postValue(r) // <- dispara evento “abre resultados”
        } catch (_: Exception) {
            _state.postValue(UiState.Error("Error en el formato de datos."))
        }
    }

    private fun calcularIndemnizacion(tipo: Int, salarioDiario: Double, ini: Date, fin: Date): Double {
        if (tipo == 1) { // Objetivo
            val anios = diasTrabajados / 365.0
            val importe = salarioDiario * (20.0 * anios)
            return min(importe, salarioDiario * 360.0)
        }
        if (tipo != 2) return 0.0
        val corte = Calendar.getInstance().apply { set(2012, Calendar.FEBRUARY, 12, 0, 0, 0) }.time
        val (pre, post) = when {
            !fin.before(corte) && !ini.after(corte) -> {
                val preEnd = Calendar.getInstance().apply { set(2012, Calendar.FEBRUARY, 11, 23, 59, 59) }.time
                daysBetween(ini, preEnd) to daysBetween(corte, fin)
            }
            fin.before(corte) -> daysBetween(ini, fin) to 0L
            else -> 0L to daysBetween(ini, fin)
        }
        val diasPre  = (pre / 365.0) * 45.0
        val diasPost = (post / 365.0) * 33.0
        var total    = diasPre + diasPost
        var tope     = 720.0
        if (diasPre > 720.0) tope = min(diasPre, 42.0 * 30.0)
        total = min(total, tope)
        return salarioDiario * total
    }

    private fun daysBetween(a: Date, b: Date): Long {
        val ms = max(0L, b.time - a.time)
        return TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS)
    }
    private fun round2(x: Double) = round(x * 100.0) / 100.0
}
