// ui/calendario/HolidayCalendarPanel.kt
package com.example.lagvis_v1.ui.calendario

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.dominio.model.holidays.PublicHolidayKt
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HolidayCalendarPanel(
    year: Int,
    provinceSlug: String,
    modifier: Modifier = Modifier,
    vm: HolidaysViewModelKt = viewModel(factory = HolidaysViewModelFactoryKt()),
    onDaySelected: (LocalDate, List<PublicHolidayKt>) -> Unit = { _, _ -> }
) {
    val state by vm.state.observeAsState(initial = UiState.Loading())

    // Carga cuando cambian los filtros
    LaunchedEffect(year, provinceSlug) {
        vm.loadByProvince(year, provinceSlug)
    }

    when (val s = state) {
        is UiState.Loading -> Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is UiState.Error -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(s.message ?: "Error", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.loadByProvince(year, provinceSlug) }) { Text("Reintentar") }
        }

        is UiState.Success -> {
            val allHolidays: List<PublicHolidayKt> = s.data.orEmpty()

            // Mapa: fecha -> lista de festivos
            val holidaysByDate: Map<LocalDate, List<PublicHolidayKt>> = remember(allHolidays) {
                allHolidays
                    .mapNotNull { h ->
                        val date = h.date?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                        if (date != null) date to h else null
                    }
                    .groupBy({ it.first }, { it.second })
            }

            // Conjunto de fechas para marcar rápidamente si es festivo
            val holidaysSet: Set<LocalDate> = remember(holidaysByDate) {
                holidaysByDate.keys
            }

            // Decide el "scope" dominante por fecha (nacional > autonomico > municipal > local > info)
            val scopeForDate: (LocalDate) -> String? = remember(holidaysByDate) {
                { date ->
                    val items = holidaysByDate[date].orEmpty()
                    if (items.isEmpty()) null else pickScope(items)
                }
            }

            HolidayCalendar(
                startMonth   = YearMonth.of(year, 1),
                endMonth     = YearMonth.of(year, 12),
                holidays     = holidaysSet,
                scopeForDate = scopeForDate,        // 👈 AHORA sí pasamos el scope
                onDayClick   = { date ->
                    onDaySelected(date, holidaysByDate[date].orEmpty())
                },
                modifier     = modifier
            )
        }
    }
}

/* ======================= Helpers de scope ======================= */

// Si ya la tienes definida en otro archivo, elimínala aquí y usa la tuya.
private fun normalizeScope(raw: String?): String =
    (raw ?: "")
        .trim()
        .lowercase()
        .replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u")

/**
 * Determina el tipo dominante del día usando prioridad:
 * nacional > autonomico > municipal > local > info
 *
 * Extrae la pista desde h.type, h.scope o h.name.
 */
private fun pickScope(items: List<PublicHolidayKt>): String? {
    val order = listOf("nacional", "autonomico", "municipal", "local", "info")

    val present = items.mapNotNull { h ->
        val src = normalizeScope( h.scope ?: h.name)
        when {
            "nacional" in src -> "nacional"
            "autonom"  in src -> "autonomico"
            "municip"  in src -> "municipal"
            "local"    in src -> "local"
            else              -> "info"
        }
    }.toSet()

    return order.firstOrNull { it in present } ?: present.firstOrNull()
}
