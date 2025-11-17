package com.example.lagvis_v1.ui.finiquitos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import com.example.lagvis_v1.ui.common.HeaderGradientParallaxSmall
import com.example.lagvis_v1.ui.theme.AppFont
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultadoFiniquitoScreen(
    resultado: DatosGeneralesFiniquitoViewModel.Resultado,
    onExportPdf: () -> Unit = {},
    onBack: () -> Unit,
) {
    var showInfo by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {

        // --- Contenido principal ---
        Scaffold(
            topBar = {
                HeaderGradientParallaxSmall(
                    title = "Resultado del cálculo",
                    subtitle = "Resumen de tu finiquito",
                    showBack = false,
                    onBack = onBack,
                    leadingIcon = Icons.Outlined.CalendarMonth
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                // Vacaciones
                item {
                    SimpleSectionCard(
                        title = "Vacaciones no disfrutadas",
                        value = formatCurrency(resultado.importeVacaciones),
                        leadingIcon = Icons.Outlined.BeachAccess
                    )
                }

                // Salario del mes
                item {
                    SimpleSectionCard(
                        title = "Salario del mes trabajado",
                        value = formatCurrency(resultado.salarioPorDiasTrabajados),
                        leadingIcon = Icons.Outlined.CalendarMonth
                    )
                }

                // Pagas extra
                item {
                    SimpleSectionCard(
                        title = "Pagas extra",
                        value = formatCurrency(resultado.pagasExtra),
                        leadingIcon = Icons.Outlined.CardGiftcard
                    )
                }

                // Finiquito + botón info
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Finiquito",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    fontFamily = AppFont
                                )
                                IconButton(onClick = { showInfo = true }) {
                                    Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = "Información finiquito"
                                    )
                                }
                            }
                            Text(
                                text = formatCurrency(resultado.totalFiniquito),
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = AppFont
                            )
                        }
                    }
                }

                // Indemnización
                item {
                    SimpleSectionCard(
                        title = "Indemnización",
                        value = formatCurrency(resultado.indemnizacion),
                        leadingIcon = Icons.Outlined.Scale
                    )
                }

                // Total a recibir
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.Inventory2, contentDescription = null)
                                Text(
                                    text = "Total a recibir",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    fontFamily = AppFont
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = formatCurrency(resultado.totalLiquidacion),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF2E7D32),
                                fontFamily = AppFont
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Aviso: cálculo estimado. La indemnización depende del tipo de despido.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = AppFont
                            )
                        }
                    }
                }

                // Exportar PDF
                item {
                    Button(
                        onClick = onExportPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentPadding = PaddingValues(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Exportar a PDF", fontFamily = AppFont)
                    }
                }
            }
        }

        // --- Overlay: scrim + tarjeta animados ---
        AnimatedVisibility(
            visible = showInfo,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .noRippleClickable { showInfo = false } // tap fuera cierra
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f))
            )
        }

        AnimatedVisibility(
            visible = showInfo,
            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
            exit  = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            InfoFiniquitoCard(onClose = { showInfo = false })
        }
    }
}

/* ---------- Reusable ---------- */

@Composable
private fun SimpleSectionCard(
    title: String,
    value: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    fontFamily = AppFont
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = AppFont
            )
        }
    }
}

@Composable
private fun InfoFiniquitoCard(onClose: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "¿Qué es el finiquito?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
                IconButton(onClick = onClose) { Icon(Icons.Outlined.Info, null) }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "El finiquito es el cálculo y la liquidación final de todas las cantidades pendientes entre empresa y persona trabajadora al terminar el contrato. Va acompañado de un documento que detalla los conceptos y el importe total a percibir (o, en casos puntuales, a devolver, por ejemplo por anticipo).",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = AppFont
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Entendido", fontFamily = AppFont) }
        }
    }
}

// Scrim sin ripple
private fun Modifier.noRippleClickable(onClick: () -> Unit) = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}

private fun formatCurrency(amount: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
    return nf.format(amount)
}

/* ---------- Preview ---------- */

@Preview(showBackground = true)
@Composable
private fun PreviewResultadoFiniquitoScreen() {
    val fake = DatosGeneralesFiniquitoViewModel.Resultado(
        salarioPorDiasTrabajados = 812.45,
        importeVacaciones = 350.0,
        pagasExtra = 420.0,
        totalFiniquito = 1582.45,
        indemnizacion = 4350.0,
        totalLiquidacion = 5932.45
    )
    ResultadoFiniquitoScreen(resultado = fake, onBack = {})
}
