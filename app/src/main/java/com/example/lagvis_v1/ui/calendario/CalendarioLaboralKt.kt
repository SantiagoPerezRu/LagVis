// ui/calendario/CalendarioLaboralKt.kt
package com.example.lagvis_v1.ui.calendario

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lagvis_v1.R
import com.example.lagvis_v1.core.ui.UiState
import com.example.lagvis_v1.core.util.AppData
import com.example.lagvis_v1.databinding.FragmentCalendarioLaboralBinding
import com.example.lagvis_v1.dominio.model.PublicHolidayKt
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarioLaboralKt : Fragment(R.layout.fragment_calendario_laboral) {

    private var _binding: FragmentCalendarioLaboralBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: HolidaysViewModelKt
    private val holidayAdapter = HolidayAdapterKt(mutableListOf())
    private val holidayList = mutableListOf<PublicHolidayKt>()

    // Estado UI
    private var selectedProvinciaName: String = ""
    private var selectedProvinciaSlug: String = ""

    // Calendario
    private lateinit var calView: CalendarView
    private val festivosSet = mutableSetOf<LocalDate>()
    private var calendarYearShown: Int = -1
    private val scopeByDate = mutableMapOf<LocalDate, String>()
    private var holidayPopup: PopupWindow? = null
    private val ES = Locale("es", "ES")
    private val DATE_FMT = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", ES)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarioLaboralBinding.inflate(inflater, container, false)

        // ---- Autocomplete PROVINCIAS ----
        val arrAdapterProvincias = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            AppData.PROVINCIAS
        )
        binding.autoCompleteTextViewComunidades.setAdapter(arrAdapterProvincias)
        binding.autoCompleteTextViewComunidades.setOnItemClickListener { _, _, _, _ ->
            selectedProvinciaName = binding.autoCompleteTextViewComunidades.text.toString()
            selectedProvinciaSlug = AppData.PROVINCIA_TO_SLUG.getOrDefault(selectedProvinciaName, "")
        }

        // ---- Autocomplete AÑOS ----
        (binding.autoCompleteTextViewAnio as MaterialAutoCompleteTextView).apply {
            setSimpleItems(R.array.anios)
            if (text.isNullOrBlank()) setText(resources.getStringArray(R.array.anios)[0], false)
        }

        // ---- Estado inicial ----
        binding.tvNoFestivos.text = "Selecciona una provincia y año, luego pulsa Buscar."
        binding.tvNoFestivos.isVisible = true
        binding.cardCalendar.isVisible = false

        // ---- CalendarView ----
        calView = binding.calendarView
        initCalendarDayBinder()
        initCalendarHeaderBinder()
        setupCalendarForYear(readYearOrCurrent())

        // ---- Botón buscar ----
        binding.btnEnviar.setOnClickListener {
            selectedProvinciaName = binding.autoCompleteTextViewComunidades.text.toString()
            selectedProvinciaSlug = AppData.PROVINCIA_TO_SLUG[selectedProvinciaName]
                ?: ""

            if (selectedProvinciaSlug.isNotEmpty()) {
                val year = readYearOrCurrent()
                vm.loadByProvince(year, selectedProvinciaSlug)
            } else {
                Toast.makeText(requireContext(), "Selecciona una provincia válida.", Toast.LENGTH_SHORT).show()
                showEmpty("Selecciona una provincia válida para ver los festivos.")
                clearCalendarMarks()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this, HolidaysViewModelFactoryKt())[HolidaysViewModelKt::class.java]

        vm.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading("Introduzca una comunidad y un año.${if (selectedProvinciaName.isBlank()) "…" else " de $selectedProvinciaName…"}")
                is UiState.Success -> renderSuccess(state.data ?: emptyList())
                is UiState.Error -> showError("Error al cargar los festivos${state.message?.let { ": $it" } ?: "."}")
            }
        }
    }

    // ------------------ UI helpers ------------------

    private fun readYearOrCurrent(): Int =
        runCatching {
            val txt = binding.autoCompleteTextViewAnio.text?.toString()?.trim().orEmpty()
            if (txt.isEmpty()) Calendar.getInstance().get(Calendar.YEAR) else txt.toInt()
        }.getOrDefault(Calendar.getInstance().get(Calendar.YEAR))

    private fun showLoading(message: String) {
        binding.tvNoFestivos.text = message
        binding.tvNoFestivos.isVisible = true
        binding.cardCalendar.isVisible = false
    }

    private fun showEmpty(message: String) {
        binding.tvNoFestivos.text = message
        binding.tvNoFestivos.isVisible = true
        binding.cardCalendar.isVisible = false
        holidayAdapter.setHolidayList(emptyList())
    }

    private fun showError(message: String) = showEmpty(message)

    private fun renderSuccess(rawHolidays: List<PublicHolidayKt>) {
        val list = rawHolidays.sortedBy { it.date ?: "" }

        if (list.isEmpty()) {
            showEmpty("No se encontraron festivos para $selectedProvinciaName.")
            clearCalendarMarks()
            return
        }

        // (Si tienes un RecyclerView visible, usa esto)
        // holidayAdapter.setHolidayList(list)

        // Actualiza calendario
        scopeByDate.clear()
        festivosSet.clear()

        list.forEach { h ->
            val dateIso = h.date
            if (dateIso?.length == 10) {
                runCatching {
                    val d = LocalDate.parse(dateIso)
                    val scope = normalizeScope(h.scope)
                    val prev = scopeByDate[d]
                    if (prev == null || scopePriority(scope) > scopePriority(prev)) {
                        scopeByDate[d] = scope
                    }
                    festivosSet.add(d)
                }
            }
        }

        setupCalendarForYear(readYearOrCurrent())
        calView.notifyCalendarChanged()

        binding.tvNoFestivos.isVisible = false
        binding.cardCalendar.isVisible = true
    }

    private fun clearCalendarMarks() {
        scopeByDate.clear()
        festivosSet.clear()
        calView.notifyCalendarChanged()
    }

    // ------------------ Kizitonwose: binders y setup ------------------

    private fun initCalendarDayBinder() {
        calView.dayBinder = object : MonthDayBinder<DayContainer> {
            override fun create(view: View) = DayContainer(view)
            override fun bind(container: DayContainer, day: CalendarDay) {
                val tv = container.textView
                tv.text = day.date.dayOfMonth.toString()

                // Atenúa días fuera del mes
                tv.alpha = if (day.position == DayPosition.MonthDate) 1f else 0.3f

                val scope = scopeByDate[day.date]
                if (scope != null && day.position == DayPosition.MonthDate) {
                    tv.setBackgroundResource(bgForScope(scope))
                    tv.setTextColor(Color.WHITE)
                    tv.setTypeface(null, Typeface.BOLD)
                } else {
                    tv.background = null
                    tv.setTextColor(0xFF222222.toInt())
                    tv.setTypeface(null, Typeface.NORMAL)
                }

                container.view.setOnClickListener {
                    if (day.position != DayPosition.MonthDate) {
                        binding.cardHolidayInfo.isVisible = false
                    } else {
                        showHolidayPopup(container.view, day.date)
                    }
                }
            }
        }
    }

    private fun initCalendarHeaderBinder() {
        calView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderContainer> {
            override fun create(view: View) = MonthHeaderContainer(view)
            override fun bind(container: MonthHeaderContainer, month: CalendarMonth) {
                val name = month.yearMonth.month
                    .getDisplayName(java.time.format.TextStyle.FULL, ES)
                val pretty = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(ES) else it.toString() }
                container.title?.text = "$pretty ${month.yearMonth.year}"
            }
        }
    }

    private fun setupCalendarForYear(year: Int) {
        if (calendarYearShown == year) return
        val start = YearMonth.of(year, 1)
        val end = YearMonth.of(year, 12)
        calView.setup(start, end, DayOfWeek.MONDAY)
        calView.scrollToMonth(start)
        calendarYearShown = year
    }

    // ------------------ Popup & utils ------------------

    private fun showHolidayPopup(anchor: View, date: LocalDate) {
        val dayHolidays = getHolidaysByDate(date)
        if (dayHolidays.isEmpty()) {
            dismissHolidayPopup(); return
        }
        dismissHolidayPopup()

        val content = layoutInflater.inflate(R.layout.popup_holiday_info, null, false)
        val tvDate = content.findViewById<TextView>(R.id.tvPopupDate)
        val tvProv = content.findViewById<TextView>(R.id.tvPopupProvince)
        val chips = content.findViewById<ChipGroup>(R.id.chipGroupPopup)

        tvDate.text = capitalizeFirst(date.format(DATE_FMT))
        tvProv.text = selectedProvinciaName

        dayHolidays.forEach { h ->
            val chip = Chip(requireContext()).apply {
                text = h.name ?: "Festivo"
                setTextColor(Color.WHITE)
                isClickable = false
                isCheckable = false
                isSingleLine = true
                ellipsize = android.text.TextUtils.TruncateAt.END
                textSize = 15f
                setEnsureMinTouchTargetSize(false)
                chipMinHeight = 36f
                chipStartPadding = 12f
                chipEndPadding = 12f
                textStartPadding = 6f
                textEndPadding = 6f
                chipCornerRadius = 24f
                setChipBackgroundColorResource(colorForScope(h.scope))
            }
            chips.addView(chip)
        }

        holidayPopup = PopupWindow(
            content,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            animationStyle = R.style.CalendarPopupAnim
        }

        content.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupW = content.measuredWidth
        val popupH = content.measuredHeight

        val loc = IntArray(2).also { anchor.getLocationOnScreen(it) }
        val anchorW = anchor.width
        val x = loc[0] + (anchorW / 2) - (popupW / 2)
        val y = loc[1] - popupH - dp(8)

        holidayPopup?.showAtLocation(anchor, Gravity.TOP or Gravity.START, x.coerceAtLeast(dp(8)), y)
    }

    private fun dismissHolidayPopup() {
        holidayPopup?.dismiss()
        holidayPopup = null
    }

    private fun getHolidaysByDate(date: LocalDate): List<PublicHolidayKt> =
        (vm.state.value as? UiState.Success<List<PublicHolidayKt>>)
            ?.data.orEmpty()
            .filter { it.date?.let { d -> runCatching { LocalDate.parse(d) }.getOrNull() == date } == true }

    private fun colorForScope(raw: String?): Int = when (normalizeScope(raw)) {
        "nacional"   -> R.color.festivo_nacional
        "autonomico" -> R.color.festivo_autonomico
        "municipal"  -> R.color.festivo_municipal
        "local"      -> R.color.festivo_local
        "info"       -> R.color.festivo_info
        else         -> R.color.festivo_info
    }

    private fun bgForScope(scope: String?): Int = when (normalizeScope(scope)) {
        "nacional"   -> R.drawable.bg_festivo_nacional
        "autonomico" -> R.drawable.bg_festivo_autonomico
        "municipal"  -> R.drawable.bg_festivo_municipal
        "local"      -> R.drawable.bg_festivo_local
        "info"       -> R.drawable.bg_festivo_info
        else         -> R.drawable.bg_festivo_info
    }

    private fun capitalizeFirst(s: String) =
        s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(ES) else it.toString() }

    private fun normalizeScope(s: String?): String {
        val t = s?.let {
            java.text.Normalizer.normalize(it, java.text.Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                .lowercase(Locale.ROOT)
                .trim()
        } ?: return "otros"
        return when {
            t == "estatal" -> "nacional"
            t.startsWith("autonom") -> "autonomico"
            t.startsWith("municip") -> "municipal"
            t.startsWith("local") || t.startsWith("provinc") -> "local"
            t.startsWith("info") -> "info"
            t in setOf("nacional", "autonomico", "municipal", "local", "info") -> t
            else -> "otros"
        }
    }

    private fun scopePriority(scope: String?): Int = when (normalizeScope(scope)) {
        "nacional" -> 5
        "autonomico" -> 4
        "municipal" -> 3
        "local" -> 2
        "info" -> 1
        else -> 0
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        dismissHolidayPopup()
        _binding = null
        super.onDestroyView()
    }

    // Containers del calendario
    class DayContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
    }
    class MonthHeaderContainer(view: View) : ViewContainer(view) {
        val title: TextView? = view.findViewById(R.id.tvMonthTitle)
    }
}
