// ui/calendario/CalendarioLaboralScreenM3.kt
package com.example.lagvis_v1.ui.calendario

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lagvis_v1.R
import com.example.lagvis_v1.dominio.model.PublicHolidayKt
import com.example.lagvis_v1.ui.auth.uicompose.ui.theme.AppFont
import com.example.lagvis_v1.ui.common.DropdownOutlinedM3
import com.example.lagvis_v1.ui.common.HeaderGradientParallax
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioLaboralScreenM3(
    años: List<String>,
    provincias: List<String>,
    onBack: () -> Unit = {},
    onDaySelected: (LocalDate) -> Unit = {},
) {
    // ---- Estado filtros / validación ----
    var añoSeleccionado by rememberSaveable { mutableStateOf("") }
    var provinciaSeleccionada by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }
    val formOk = añoSeleccionado.isNotBlank() && provinciaSeleccionada.isNotBlank()

    // ---- Estado hoja inferior (detalle de festivo) ----
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var sheetDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var sheetItems by remember { mutableStateOf<List<PublicHolidayKt>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ---- Formato fecha ES ----
    val es = remember { Locale("es", "ES") }
    val dateFmt = remember { DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", es) }

    // ========================== UI ==========================
    Scaffold(
        topBar = {
            HeaderGradientParallax(
                title = "Calendario\nlaboral",
                subtitle = "Selecciona provincia y año",
                showBack = false,
                onBack = onBack,
                leadingIcon = Icons.Outlined.CalendarMonth
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ===== Filtros =====
            item {
                Column {
                    Text(
                        "Año",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = AppFont
                    )
                    Spacer(Modifier.height(8.dp))
                    DropdownOutlinedM3(
                        value = añoSeleccionado,
                        options = años,
                        onSelect = {
                            añoSeleccionado = it
                            if (showError) showError = false
                        },
                        placeholder = "Selecciona año",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Provincia",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = AppFont
                    )
                    Spacer(Modifier.height(8.dp))
                    DropdownOutlinedM3(
                        value = provinciaSeleccionada,
                        options = provincias,
                        onSelect = {
                            provinciaSeleccionada = it
                            if (showError) showError = false
                        },
                        placeholder = "Selecciona provincia",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showError) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Debes seleccionar año y provincia",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            fontFamily = AppFont
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (formOk) {
                                mostrarCalendario = true
                            } else {
                                showError = true
                            }
                        },
                        enabled = formOk,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Consultar", fontFamily = AppFont)
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {}
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Verás el calendario con los festivos resaltados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = AppFont
                        )
                    }
                }
            }

            // ===== Calendario + Leyenda =====
            if (mostrarCalendario) {
                item {
                    val yearInt = añoSeleccionado.toIntOrNull() ?: Year.now().value
                    val provinceSlug = slugify(provinciaSeleccionada)

                    // 👇 Aquí debes inyectar tu mapa real desde el VM:
                    // holidaysByDate = holidays.groupBy { it.date.toLocalDate() }
                    val holidaysByDate: Map<LocalDate, List<PublicHolidayKt>> by remember {
                        mutableStateOf(emptyMap())
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = "Calendario $provinciaSeleccionada $añoSeleccionado",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                fontFamily = AppFont
                            )
                            Spacer(Modifier.height(8.dp))

                            // Panel que pinta el calendario con fondo por tipo de festivo
                            HolidayCalendarPanel( //
                                year = yearInt,
                                provinceSlug = provinceSlug,
                                onDaySelected = { date, items ->
                                    sheetDate = date
                                    sheetItems = items
                                    showSheet = items.isNotEmpty()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )


                            Spacer(Modifier.height(12.dp))
                            LegendCard()
                        }
                    }
                }
            }
        }

        // ===== BottomSheet con detalle del festivo =====
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                val dateText = sheetDate?.let {
                    it.format(dateFmt).replaceFirstChar { c ->
                        if (c.isLowerCase()) c.titlecase(es) else c.toString()
                    }
                }.orEmpty()

                Column(Modifier.padding(16.dp)) {
                    Text(
                        dateText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        fontFamily = AppFont
                    )
                    if (provinciaSeleccionada.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            provinciaSeleccionada,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = AppFont
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    // Chips con nombre de festivo
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sheetItems.forEach { h ->
                            AssistChip(
                                onClick = { /* noop */ },
                                label = { Text(h.name ?: h.localName ?: "Festivo") }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

/* ======================= LEYENDA ======================= */

@Composable
private fun LegendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Colores de festivos",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                fontFamily = AppFont
            )
            Spacer(Modifier.height(8.dp))
            LegendRow(color = colorResource(id = colorForScope("nacional")),   label = "Nacional")
            LegendRow(color = colorResource(id = colorForScope("autonomico")), label = "Autonómica")
            LegendRow(color = colorResource(id = colorForScope("municipal")),  label = "Municipal")
            LegendRow(color = colorResource(id = colorForScope("local")),      label = "Local / Provincial")
        }
    }
}

@Composable
private fun LegendRow(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            modifier = Modifier.size(14.dp),
            shape = CircleShape,
            color = color
        ) {}
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = AppFont
        )
    }
}

/* ======================= HELPERS ======================= */

fun slugify(input: String): String {
    if (input.isBlank()) return ""
    val normalized =
        java.text.Normalizer.normalize(input.lowercase(Locale("es")), java.text.Normalizer.Form.NFD)
    return normalized
        .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        .replace("[^a-z0-9]+".toRegex(), "-")
        .trim('-')
}

private fun normalizeScope(raw: String?): String =
    (raw ?: "")
        .trim()
        .lowercase()
        .replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u")

/** Prioridad: nacional > autonomico > municipal > local > info */
private fun pickScope(items: List<PublicHolidayKt>): String? {
    val order = listOf("nacional", "autonomico", "municipal", "local", "info")
    // Intenta detectar el scope desde type/scope/name
    val present = items.mapNotNull { h ->
        val src = normalizeScope(h.scope ?: h.name)
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

@ColorRes
private fun colorForScope(scope: String?): Int = when (normalizeScope(scope)) {
    "nacional"   -> R.color.festivo_nacional
    "autonomico" -> R.color.festivo_autonomico
    "municipal"  -> R.color.festivo_municipal
    "local"      -> R.color.festivo_local
    "info"       -> R.color.festivo_info
    else         -> R.color.festivo_info
}

@DrawableRes
private fun bgForScope(scope: String?): Int = when (normalizeScope(scope)) {
    "nacional"   -> R.drawable.bg_festivo_nacional
    "autonomico" -> R.drawable.bg_festivo_autonomico
    "municipal"  -> R.drawable.bg_festivo_municipal
    "local"      -> R.drawable.bg_festivo_local
    "info"       -> R.drawable.bg_festivo_info
    else         -> R.drawable.bg_festivo_info
}

/* ========== CALENDARIO: Kizitonwose Compose (HorizontalCalendar) ========== */

@Composable
fun HolidayCalendar(
    startMonth: YearMonth,
    endMonth: YearMonth,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    holidays: Set<LocalDate>,
    scopeForDate: (LocalDate) -> String?,   // 👈 clave para pintar fondo por tipo
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = startMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Column(
        modifier
            .fillMaxWidth()
            .height(340.dp) // altura acotada para scroll suave
    ) {
        val ym = state.firstVisibleMonth.yearMonth
        MonthHeader(ym)
        Spacer(Modifier.height(8.dp))
        WeekdayRow(firstDayOfWeek)

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                val enabled = day.position == DayPosition.MonthDate
                val date = day.date
                val isHoliday = holidays.contains(date)
                val scope = if (enabled && isHoliday) scopeForDate(date) else null

                DayCell(
                    day = day,
                    isHoliday = isHoliday,
                    enabled = enabled,
                    scope = scope,
                    onClick = { if (enabled) onDayClick(date) }
                )
            }
        )
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth) {
    val name = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.titlecase(Locale("es")) }
    Text(
        "$name ${yearMonth.year}",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        fontFamily = AppFont
    )
}

@Composable
private fun WeekdayRow(firstDayOfWeek: DayOfWeek) {
    val days = daysOfWeek(firstDayOfWeek)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { d ->
            Text(
                d.getDisplayName(TextStyle.SHORT, Locale("es")),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
                fontFamily = AppFont
            )
        }
    }
}


@Composable
private fun DayCell(
    day: CalendarDay,
    isHoliday: Boolean,
    enabled: Boolean,
    scope: String?,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f
    val context = LocalContext.current

    val bgPainter = remember(scope) {
        scope?.let { sc ->
            AppCompatResources.getDrawable(context, bgForScope(sc))
        }
    }?.let { drawable ->
        rememberDrawablePainter(drawable)
    }

    val base = Modifier
        .aspectRatio(1f)
        .padding(2.dp)
        .clip(RoundedCornerShape(10.dp))
        .then(
            if (bgPainter != null) {
                Modifier.paint(
                    painter = bgPainter,
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Modifier
            }
        )
        .clickable(enabled = enabled) { onClick() }
        .padding(6.dp)

    Column(
        modifier = base,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(alpha),
            fontFamily = AppFont
        )
    }
}

