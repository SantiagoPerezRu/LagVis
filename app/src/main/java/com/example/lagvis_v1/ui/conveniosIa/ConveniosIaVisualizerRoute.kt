package com.example.lagvis_v1.ui.conveniosIa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioUiModel
import com.example.lagvis_v1.ui.convenio.visualizer.ConvenioVisualizerWithEdgeHeader // Reutilizamos el Composable de presentación compartido

/**
 * Ruta de Visualización específica para el contenido generado por IA.
 * Recibe el modelo de datos ya procesado (ConvenioUiModel) del ConveniosIaViewModel
 * y lo pasa al componente de presentación genérico.
 */
@Composable
fun ConveniosIaVisualizerRoute(
    data: ConvenioUiModel,
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    onAction: () -> Unit,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Usamos el componente de presentación (UI) base, pero con la firma de la nueva ruta.
    ConvenioVisualizerWithEdgeHeader(
        data = data,
        title = title,
        subtitle = subtitle,
        onBack = onBack,
        onAction = onAction,
        onRate = onRate,
        modifier = modifier
    )
}
