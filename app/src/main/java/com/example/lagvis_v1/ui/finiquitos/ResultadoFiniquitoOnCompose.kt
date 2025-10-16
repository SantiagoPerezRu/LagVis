package com.example.lagvis_v1.ui.finiquitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2 // para “Total”
import androidx.compose.material.icons.outlined.Scale // alternativa a Balance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.ui.theme.AppFont
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultadoFiniquitoScreen(
    resultado: DatosGeneralesFiniquitoViewModel.Resultado,
    onInfoFiniquito: () -> Unit = {},
    onExportPdf: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            // Usa tu header propio. Si prefieres otro, cámbialo aquí.
            HeaderGradientParallax(
                title = "Resultado del cálculo",
                subtitle = "Resumen de tu finiquito",
                showBack = true,
                onBack = {}, // inyecta nav.popBackStack() si procede
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

            // Vacaciones 🌴 -> Icons.Outlined.BeachAccess
            item {
                SimpleSectionCard(
                    title = "Vacaciones no disfrutadas",
                    value = formatCurrency(resultado.importeVacaciones),
                    leadingIcon = Icons.Outlined.BeachAccess
                )
            }

            // Salario del mes 🗓️ -> Icons.Outlined.CalendarMonth
            item {
                SimpleSectionCard(
                    title = "Salario del mes trabajado",
                    value = formatCurrency(resultado.salarioPorDiasTrabajados),
                    leadingIcon = Icons.Outlined.CalendarMonth
                )
            }

            // Pagas extra 🎁 -> Icons.Outlined.CardGiftcard
            item {
                SimpleSectionCard(
                    title = "Pagas extra",
                    value = formatCurrency(resultado.pagasExtra),
                    leadingIcon = Icons.Outlined.CardGiftcard
                )
            }

            // Finiquito + botón info -> Icons.Outlined.Info
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Finiquito",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                fontFamily = AppFont
                            )
                            IconButton(onClick = onInfoFiniquito) {
                                Icon(Icons.Outlined.Info, contentDescription = "Información finiquito")
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

            // Indemnización ⚖️ -> Icons.Outlined.Scale
            item {
                SimpleSectionCard(
                    title = "Indemnización",
                    value = formatCurrency(resultado.indemnizacion),
                    leadingIcon = Icons.Outlined.Scale
                )
            }

            // Total 📦 -> Icons.Outlined.Inventory2
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
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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

private fun formatCurrency(amount: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
    return nf.format(amount)
}

/* ---------- Preview ---------- */

@Preview(showBackground = true)
@Composable
private fun PreviewResultadoFiniquitoScreen_M3Icons() {
    val fake = DatosGeneralesFiniquitoViewModel.Resultado(
        salarioPorDiasTrabajados = 812.45,
        importeVacaciones = 350.0,
        pagasExtra = 420.0,
        totalFiniquito = 1582.45,
        indemnizacion = 4350.0,
        totalLiquidacion = 5932.45
    )
    ResultadoFiniquitoScreen(
        resultado = fake,
        onInfoFiniquito = {},
        onExportPdf = {}
    )
}
