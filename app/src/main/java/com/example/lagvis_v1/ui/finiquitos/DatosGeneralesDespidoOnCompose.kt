// ui/despidos/CalculadoraDespidosInlineScreen.kt
package com.example.lagvis_v1.ui.despidos

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.ui.theme.AppFont
import com.example.lagvis_v1.ui.theme.LagVis_V1Theme
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import com.example.lagvis_v1.ui.finiquitos.DatosGeneralesFiniquitoViewModel
import com.example.lagvis_v1.ui.finiquitos.DatosGeneralesFiniquitoViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraDespidosInlineScreen(
    onBack: () -> Unit = {},
    vm: DatosGeneralesFiniquitoViewModel = viewModel(factory = DatosGeneralesFiniquitoViewModelFactory())
) {
    // Estado UI
    val es = remember { Locale("es", "ES") }
    val dateFmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", es) }

    var fechaInicio by remember { mutableStateOf<LocalDate?>(null) }
    var fechaFin by remember { mutableStateOf<LocalDate?>(null) }
    var salarioDiarioText by remember { mutableStateOf("") }
    var diasVacacionesText by remember { mutableStateOf("0") }
    var posPagas by remember { mutableStateOf(2) } // por defecto prorrateadas
    var tipoDespido by remember { mutableStateOf(1) } // por defecto objetivo

    var showPickerInicio by remember { mutableStateOf(false) }
    var showPickerFin by remember { mutableStateOf(false) }

    val salarioDiario = salarioDiarioText.toDoubleOrNull()
    val formOk = fechaInicio != null && fechaFin != null && salarioDiario != null

    val state by vm.state.observeAsState(UiState.Error("Selecciona datos y pulsa Calcular"))

    Scaffold(
        topBar = {
            HeaderGradientParallax(
                title = "Calculadora\nde Despidos",
                subtitle = "Introduce datos y calcula",
                showBack = true,
                onBack = onBack,
                leadingIcon = Icons.Outlined.Gavel
            )
        }
    ) { padding ->
        val scroll = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha inicio
            Card {
                Column(Modifier.padding(16.dp)) {
                    LabelWithIcon(Icons.Outlined.CalendarMonth, "Fecha de Inicio del Contrato")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fechaInicio?.format(dateFmt) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("dd/MM/yyyy", fontFamily = AppFont) },
                        trailingIcon = { TextButton({ showPickerInicio = true }) { Text("Elegir") } },
                        textStyle = LocalTextStyle.current.copy(fontFamily = AppFont)
                    )
                }
            }

            // Fecha fin
            Card {
                Column(Modifier.padding(16.dp)) {
                    LabelWithIcon(Icons.Outlined.CalendarMonth, "Fecha de Fin del Contrato")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fechaFin?.format(dateFmt) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("dd/MM/yyyy", fontFamily = AppFont) },
                        trailingIcon = { TextButton({ showPickerFin = true }) { Text("Elegir") } },
                        textStyle = LocalTextStyle.current.copy(fontFamily = AppFont)
                    )
                }
            }

            // Salario diario
            Card {
                Column(Modifier.padding(16.dp)) {
                    LabelWithIcon(Icons.Outlined.AttachMoney, "Salario Diario (€)")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = salarioDiarioText,
                        onValueChange = { salarioDiarioText = it },
                        placeholder = { Text("Ej: 60.00", fontFamily = AppFont) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = AppFont)
                    )
                }
            }

            // Días de vacaciones pendientes
            Card {
                Column(Modifier.padding(16.dp)) {
                    LabelWithIcon(Icons.Outlined.BeachAccess, "Días de vacaciones pendientes")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = diasVacacionesText,
                        onValueChange = { diasVacacionesText = it },
                        placeholder = { Text("0", fontFamily = AppFont) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontFamily = AppFont)
                    )
                }
            }

            // Pagas (12/14/prorr)
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabelWithIcon(Icons.Outlined.Payments, "Pagas")
                    SingleChoiceChips(
                        options = listOf("12", "14 (no prorr.)", "Prorrateadas"),
                        selectedIndex = posPagas,
                        onSelected = { posPagas = it }
                    )
                }
            }

            // Tipo de despido
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabelWithIcon(Icons.Outlined.Gavel, "Tipo de despido")
                    SingleChoiceChips(
                        options = listOf("Discip./Proced.", "Objetivo", "Improcedente", "Nulo"),
                        selectedIndex = tipoDespido,
                        onSelected = { tipoDespido = it }
                    )
                }
            }

            // Botón Calcular
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    enabled = formOk,
                    onClick = {
                        vm.setFechasContrato(
                            fechaInicio!!.format(dateFmt),
                            fechaFin!!.format(dateFmt)
                        )
                        val salarioAnual = (salarioDiario ?: return@Button) * 365.0
                        vm.calcular(
                            salarioAnual = "%.2f".format(Locale.US, salarioAnual),
                            diasVacaciones = diasVacacionesText.ifBlank { "0" },
                            posicionPagas = posPagas,
                            tipoDespido = tipoDespido
                        )
                        // NO navegues aquí. El Host cambiará de pantalla al recibir el evento del VM.
                    }
                ) { Text("Calcular", fontFamily = AppFont) }

            }

            // Estado / Resultados inline
            when (val s = state) {
                is UiState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                is UiState.Error -> Text(
                    s.message ?: "Introduce datos y pulsa Calcular",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = AppFont
                )
                is UiState.Success -> {
                    val r = s.data
                    // Resultados
                    ResultCard("Vacaciones no disfrutadas", "€ ${"%.2f".format(r.importeVacaciones)}")
                    ResultCard("Salario mes proporcional", "€ ${"%.2f".format(r.salarioPorDiasTrabajados)}")
                    ResultCard("Pagas extra (devengo)", "€ ${"%.2f".format(r.pagasExtra)}")
                    ResultCard("Finiquito (subtotal)", "€ ${"%.2f".format(r.totalFiniquito)}")
                    ResultCard("Indemnización", "€ ${"%.2f".format(r.indemnizacion)}")
                    ElevatedCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("TOTAL a recibir", style = MaterialTheme.typography.titleMedium, fontFamily = AppFont)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "€ ${"%.2f".format(r.totalLiquidacion)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = AppFont
                            )
                        }
                    }
                }
            }
        }
    }

    // DatePickers
    if (showPickerInicio) {
        LagVisDatePickerDialog(
            initial = fechaInicio ?: LocalDate.now(),
            onDismiss = { showPickerInicio = false },
            onPick = {
                fechaInicio = it
                if (fechaFin != null && fechaFin!!.isBefore(it)) fechaFin = null
            }
        )
    }
    if (showPickerFin) {
        LagVisDatePickerDialog(
            initial = fechaFin ?: (fechaInicio ?: LocalDate.now()),
            onDismiss = { showPickerFin = false },
            onPick = { fechaFin = it }
        )
    }
}

/* ---------- Helpers UI ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LagVisDatePickerDialog(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onPick: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    val date = java.time.Instant.ofEpochMilli(millis)
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    onPick(date)
                }
                onDismiss()
            }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) { DatePicker(state = state) }
}

@Composable
private fun LabelWithIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = AppFont
        )
    }
}

@Composable
private fun SingleChoiceChips(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { idx, label ->
            FilterChip(
                selected = selectedIndex == idx,
                onClick = { onSelected(idx) },
                label = { Text(label, fontFamily = AppFont) }
            )
        }
    }
}

@Composable
private fun ResultCard(label: String, value: String) {
    ElevatedCard {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontFamily = AppFont)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontFamily = AppFont)
        }
    }
}

/* ---------- Previews ---------- */
@Preview(showBackground = true, showSystemUi = true, name = "Despidos Inline - Light")
@Composable
private fun Preview_Despidos_Light() {
    LagVis_V1Theme { CalculadoraDespidosInlineScreen() }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Despidos Inline - Dark"
)
@Composable
private fun Preview_Despidos_Dark() {
    LagVis_V1Theme { CalculadoraDespidosInlineScreen() }
}
