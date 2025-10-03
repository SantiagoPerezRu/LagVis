// file: app/src/main/java/com/example/lagvis_v1/ui/convenio/ConvenioVisualizerWithEdgeHeader.kt
package com.example.lagvis_v1.ui.convenio

import android.app.Application
import android.content.res.Configuration
import android.widget.RatingBar
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.common.HeaderEdgeToEdge

/* ------------ Modelo de datos de la pantalla ------------ */
data class ConvenioUiModel(
    val titulo: String = "",
    val resumenGeneral: String = "",
    val diasVacaciones: String = "",
    val observacionesVacaciones: String = "",
    val numeroFestivos: String = "",
    val detalleFestivos: String = "",
    val regulacionHorasExtra: String = "",
    val salarioInfo: String = "",
    val salarioAproximado: String = "",
    val licenciaMatrimonio: String = "",
    val licenciaFallecimiento: String = "",
    val licenciaFormacion: String = "",
    val licenciaOtros: String = "",
    val coberturaSeguro: String = "",
    val importeSeguro: String = "",
    val igualdad: String = "",
    val saludLaboral: String = "",
    val conciliacion: String = "",
    val representacion: String = "",
    val detalleManutencion: String = "",
)

/* ------------ ROUTE: carga desde VM (cache-first) y pinta estados ------------ */
@Composable
fun ConvenioVisualizerRoute(
    archivo: String,
    title: String = "LagVis | Tu convenio, resumido.",
    subtitle: String? = "Convenio seleccionado",
    imageRes: Int? = null,
    darkScrim: Boolean = true,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as Application
    val visualVm = viewModel<ConvenioVisualizerViewModel>(
        factory = ConvenioVisualizerViewModelFactory(app)
    )

    // ✅ usar visualVm
    LaunchedEffect(archivo) { visualVm.load(archivo) }

    // ✅ acceder a visualVm.state (no .ui)
    val state: UiState<ConvenioUiModel> by visualVm.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is UiState.Loading<*> -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error<*> -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message ?: "Error al cargar el convenio")
                    Spacer(Modifier.height(12.dp))
                    // ✅ usar visualVm
                    Button(onClick = { visualVm.load(archivo) }) { Text("Reintentar") }
                }
            }
        }
        is UiState.Success<ConvenioUiModel> -> {
            ConvenioVisualizerWithEdgeHeader(
                data = s.data,
                modifier = modifier,
                title = title,
                subtitle = subtitle,
                imageRes = imageRes,
                darkScrim = darkScrim,
                onBack = onBack,
                onAction = onAction,
                onRate = onRate
            )
        }
    }
}


/* ------------ Pantalla presentacional ------------ */
@Composable
fun ConvenioVisualizerScreen(
    data: ConvenioUiModel,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    onRate: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val interop = rememberNestedScrollInteropConnection()
    val ctx = LocalContext.current
    var rating by remember { mutableStateOf(0) }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(interop),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item { header() }

        if (data.titulo.isNotBlank()) {
            item {
                Text(
                    text = data.titulo,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
        }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("📝 Resumen General"); BodyText(data.resumenGeneral)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("🏖️ Vacaciones")
            BodyText(data.diasVacaciones); BodyText(data.observacionesVacaciones)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("🎉 Festivos")
            BodyText(data.numeroFestivos); BodyText(data.detalleFestivos)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("⏱️ Horas Extraordinarias"); BodyText(data.regulacionHorasExtra)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("💰 Salario"); BodyText(data.salarioInfo); BodyText(data.salarioAproximado)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("🗓️ Licencias")
            Subheader("Retribuidas:"); BodyText(data.licenciaMatrimonio); BodyText(data.licenciaFallecimiento)
            Spacer(Modifier.height(12.dp))
            Subheader("No Retribuidas:"); BodyText(data.licenciaFormacion); BodyText(data.licenciaOtros)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("💼 Seguro"); BodyText(data.coberturaSeguro); BodyText(data.importeSeguro)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("⚖️ Derechos Generales")
            BodyText(data.igualdad); BodyText(data.saludLaboral); BodyText(data.conciliacion); BodyText(data.representacion)
        } }

        item { ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionTitle("🍽️ Manutención"); BodyText(data.detalleManutencion)
        } }

        // Valoración con AndroidView
        item {
            ScreenCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    "Valore el convenio colectivo que ha leído. Nos importa tu opinión de cara a futuras mejoras. ¡Gracias!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    AndroidView(
                        factory = { context ->
                            RatingBar(context).apply {
                                numStars = 5; stepSize = 1.0f; rating = 0
                                setOnRatingBarChangeListener { _, r, _ -> rating = r.toInt() }
                            }
                        },
                        update = { it.rating = rating.toFloat() }
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = {
                        if (rating <= 0) {
                            Toast.makeText(ctx, "Por favor, selecciona al menos una estrella.", Toast.LENGTH_SHORT).show()
                        } else onRate(rating)
                    }) { Text("Enviar") }
                }
            }
        }
    }
}

/* ------------ Wrapper que usa HeaderEdgeToEdge como header ------------ */
@Composable
fun ConvenioVisualizerWithEdgeHeader(
    data: ConvenioUiModel,
    modifier: Modifier = Modifier,
    title: String = "LagVis | Tu convenio, resumido.",
    subtitle: String? = null,
    imageRes: Int? = null,
    darkScrim: Boolean = true,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    onRate: (Int) -> Unit
) {
    ConvenioVisualizerScreen(
        data = data,
        modifier = modifier,
        header = {
            HeaderEdgeToEdge(
                title = title,
                subtitle = subtitle ?: "",
                imageRes = imageRes,
                darkScrim = darkScrim,
                onBack = onBack,
                onAction = onAction,
                leadingLogoRes = R.drawable.outline_cases_24
            )
        },
        onRate = onRate
    )
}

/* ================== Helpers UI ================== */

@Composable
private fun ScreenCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun SectionTitle(text: String) {
    if (text.isNotBlank()) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun Subheader(text: String) {
    if (text.isNotBlank()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun BodyText(text: String) {
    if (text.isNotBlank()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

/* ================== Preview ================== */

@Preview(
    name = "Visualizer + EdgeHeader (Light)",
    showBackground = true,
    widthDp = 360, heightDp = 800
)
@Preview(
    name = "Visualizer + EdgeHeader (Dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 360, heightDp = 800
)
@Composable
private fun Preview_ConvenioVisualizerWithEdgeHeader() {
    val sample = ConvenioUiModel(
        titulo = "Convenio Colectivo de Ejemplo",
        resumenGeneral = "Resumen breve del convenio.",
        diasVacaciones = "30 días naturales por año.",
        numeroFestivos = "14 festivos anuales.",
        regulacionHorasExtra = "Se compensan con descanso equivalente.",
        salarioInfo = "Tabla salarial 2025.",
        salarioAproximado = "Salario Aproximado: 1.450€ - 1.900€"
    )

    MaterialTheme {
        ConvenioVisualizerWithEdgeHeader(
            data = sample,
            title = "LagVis | Tu convenio, resumido",
            subtitle = "Convenio seleccionado",
            imageRes = null,
            darkScrim = true,
            onBack = {},
            onAction = {},
            onRate = { }
        )
    }
}
