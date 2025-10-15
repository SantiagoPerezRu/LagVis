package com.example.lagvis_v1.ui.conveniosIa

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.core.ui.UiState
// Ya no importamos ConvenioVisualizerRoute del otro paquete

@Composable
fun ConveniosIaHost(
    onBack: () -> Unit, // Navegación fuera del flujo de IA
    onRate: (Int) -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as Application
    val ctx = LocalContext.current

    // Usamos la factoría que creamos antes
    val iaVm: ConveniosIaViewModel = viewModel(factory = ConveniosIaViewModelFactory(app))

    val navState by iaVm.nav.collectAsStateWithLifecycle()
    val visualState by iaVm.state.collectAsStateWithLifecycle()

    val isViewingResult = visualState is UiState.Success

    // Reacción a eventos de Navegación/Error
    LaunchedEffect(navState) {
        when (val s = navState) {
            is UiState.Error -> {
                Log.e("ConveniosIaHost", "Error IA en navegación: ${s.message}")
                Toast.makeText(ctx, "❌ ERROR IA: ${s.message}", Toast.LENGTH_LONG).show()
                iaVm.consumeNav()
            }
            is UiState.Success -> {
                Log.d("ConveniosIaHost", "Resumen IA completado. Navegando a visualizador.")
                iaVm.consumeNav()
            }
            else -> Unit
        }
    }

    if (isViewingResult) {
        // 1. Muestra LA NUEVA pantalla de visualización
        val data = (visualState as UiState.Success).data

        ConveniosIaVisualizerRoute( // <-- Usando la nueva ruta
            data = data,
            title = data.titulo.takeIf { it.isNotBlank() } ?: "Resumen de Convenio (IA)",
            subtitle = "Generado por IA a partir del PDF",
            onBack = {
                Log.d("ConveniosIaHost", "Volviendo al selector de PDF.")
                iaVm.resetState() // Limpiamos el estado para volver al selector de PDF
            },
            onAction = { /* ... */ },
            onRate = onRate
        )
    } else {
        // 2. Muestra la pantalla de selección de PDF
        val isLoading = visualState is UiState.Loading

        ConveniosIaScreen(
            onSubmit = iaVm::summarizeAndLoad,
            isLoading = isLoading
        )
    }
}
