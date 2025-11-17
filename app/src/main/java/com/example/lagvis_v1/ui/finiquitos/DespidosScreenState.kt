// DespidosHost.kt (o junto a tu Screen)
package com.example.lagvis_v1.ui.despidos

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.finiquitos.DatosGeneralesFiniquitoViewModel
import com.example.lagvis_v1.ui.finiquitos.ResultadoFiniquitoScreen

sealed class DespidosScreenState {
    object Form : DespidosScreenState()
    data class Result(val data: DatosGeneralesFiniquitoViewModel.Resultado) : DespidosScreenState()
}

/** ÚNICA entrada desde el Drawer. Sin Activities nuevas, sin NavHost. */
@Composable
fun CalculadoraDespidosHost(
    vm: DatosGeneralesFiniquitoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var current by remember { mutableStateOf<DespidosScreenState>(DespidosScreenState.Form) }

    // Escucha el "evento" del VM y cambia de pantalla
    val openEvent by vm.openResults.observeAsState()
    LaunchedEffect(openEvent) {
        openEvent?.let { res ->
            current = DespidosScreenState.Result(res)
            vm.consumeOpenResults()
        }
    }

    when (val s = current) {
        is DespidosScreenState.Form -> CalculadoraDespidosInlineScreen(
            vm = vm,
            onBack = {}, // si quieres cerrar
        )
        is DespidosScreenState.Result -> ResultadoFiniquitoScreen(
            resultado = s.data,
            //onInfoFiniquito = { /* opcional */ },
            onExportPdf = { /* opcional */ },
            onBack = { current = DespidosScreenState.Form }
        )
    }
}
