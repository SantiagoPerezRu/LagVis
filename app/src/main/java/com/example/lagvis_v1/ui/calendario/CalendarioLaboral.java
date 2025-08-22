package com.example.lagvis_v1.ui.calendario;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.AppData;
import com.example.lagvis_v1.databinding.FragmentCalendarioLaboralBinding;
import com.example.lagvis_v1.dominio.PublicHoliday;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.core.CalendarMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.time.format.DateTimeFormatter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
public class CalendarioLaboral extends Fragment {

    private FragmentCalendarioLaboralBinding binding;   // ViewBinding
    private HolidayAdapter holidayAdapter;
    private final List<PublicHoliday> holidayList = new ArrayList<>();

    private HolidaysViewModel vm;

    // Estado UI
    private String selectedProvinciaName = "";
    private String selectedProvinciaSlug = "";

    // Calendario Kizitonwose
    private CalendarView calView;
    private final Set<LocalDate> festivosSet = new HashSet<>();
    private int calendarYearShown = -1;
    private final java.util.Map<java.time.LocalDate, String> scopeByDate = new java.util.HashMap<>();
    private PopupWindow holidayPopup;
    private final Locale ES = new Locale("es","ES");
    private final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", ES);

    public CalendarioLaboral() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarioLaboralBinding.inflate(inflater, container, false);

        // ---- RecyclerView ----
        //binding.recyclerViewFestivos.setLayoutManager(new LinearLayoutManager(requireContext()));
        holidayAdapter = new HolidayAdapter(holidayList);
        //binding.recyclerViewFestivos.setAdapter(holidayAdapter);

        // ---- Autocomplete PROVINCIAS ----
        ArrayAdapter<String> arrAdapterProvincias = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                AppData.PROVINCIAS
        );
        binding.autoCompleteTextViewComunidades.setAdapter(arrAdapterProvincias);
        binding.autoCompleteTextViewComunidades.setOnItemClickListener((adapterView, v, i, l) -> {
            selectedProvinciaName = binding.autoCompleteTextViewComunidades.getText().toString();
            selectedProvinciaSlug = AppData.PROVINCIA_TO_SLUG.getOrDefault(selectedProvinciaName, "");
        });

        MaterialAutoCompleteTextView actv =
                (MaterialAutoCompleteTextView) binding.autoCompleteTextViewAnio;

        // Carga directa desde el string-array
        actv.setSimpleItems(R.array.anios);

        // Selección por defecto (opcional, sin abrir el dropdown)
        actv.setText(getResources().getStringArray(R.array.anios)[0], false);

        // Listener de selección
        actv.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = (String) parent.getItemAtPosition(position);

        });


        // ---- Estado inicial ----
        binding.tvNoFestivos.setText("Selecciona una provincia y año, luego pulsa Buscar.");
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        // binding.recyclerViewFestivos.setVisibility(View.GONE);
        binding.cardCalendar.setVisibility(View.GONE);

        // ---- CalendarView: registrar binders ANTES de medir/retornar la vista ----
        calView = binding.calendarView;

        // Binder de DÍA (usa tu método existente)
        initCalendarDayBinder();

        calView.setMonthHeaderBinder(
                new com.kizitonwose.calendar.view.MonthHeaderFooterBinder<MonthHeaderContainer>() {
                    @NonNull
                    @Override
                    public MonthHeaderContainer create(@NonNull View view) {
                        return new MonthHeaderContainer(view);
                    }

                    @Override
                    public void bind(@NonNull MonthHeaderContainer container,
                                     @NonNull CalendarMonth month) { // <-- aquí CalendarMonth
                        if (container.title != null) {
                            String name = month.getYearMonth().getMonth()
                                    .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es"));
                            String pretty = name.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + name.substring(1);
                            container.title.setText(pretty + " " + month.getYearMonth().getYear());
                        }
                    }
                }
        );


        // Rango del calendario (hazlo DESPUÉS de registrar binders)
        setupCalendarForYear(readYearOrCurrent());

        // ---- Botón buscar ----
        binding.btnEnviar.setOnClickListener(v2 -> {
            selectedProvinciaName = binding.autoCompleteTextViewComunidades.getText().toString();
            selectedProvinciaSlug = AppData.PROVINCIA_TO_SLUG.get(selectedProvinciaName);

            if (selectedProvinciaSlug != null && !selectedProvinciaSlug.isEmpty()) {
                int year = readYearOrCurrent();
                // VM se inicializa en onViewCreated, pero el click vendrá después — OK.
                vm.loadByProvince(year, selectedProvinciaSlug);
            } else {
                Toast.makeText(getContext(), "Selecciona una provincia válida.", Toast.LENGTH_SHORT).show();
                binding.tvNoFestivos.setText("Selecciona una provincia válida para ver los festivos.");
                binding.tvNoFestivos.setVisibility(View.VISIBLE);
                //binding.recyclerViewFestivos.setVisibility(View.GONE);
                binding.cardCalendar.setVisibility(View.GONE);
                holidayAdapter.setHolidayList(new ArrayList<>());
                festivosSet.clear();
                if (calView != null) calView.notifyCalendarChanged();
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel
        vm = new ViewModelProvider(this, new HolidaysViewModelFactory())
                .get(HolidaysViewModel.class);

        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                showLoading("Cargando festivos" + (selectedProvinciaName.isEmpty() ? "…" : " de " + selectedProvinciaName + "…"));
            } else if (state instanceof UiState.Success) {
                @SuppressWarnings("unchecked")
                List<PublicHoliday> raw = ((UiState.Success<List<PublicHoliday>>) state).data;
                renderSuccess(raw);
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).message;
                renderError("Error al cargar los festivos" + (msg != null ? (": " + msg) : "."));
            }
        });
    }

    private int readYearOrCurrent() {
        try {
            String yStr = binding.autoCompleteTextViewAnio.getText() != null ? binding.autoCompleteTextViewAnio.getText().toString().trim() : "";
            if (yStr.isEmpty()) return Calendar.getInstance().get(Calendar.YEAR);
            return Integer.parseInt(yStr);
        } catch (Exception e) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    private void showLoading(String message) {
        binding.tvNoFestivos.setText(message);
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        // binding.recyclerViewFestivos.setVisibility(View.GONE);
        binding.cardCalendar.setVisibility(View.GONE);
    }

    private void renderSuccess(List<PublicHoliday> rawHolidays) {
        // Copia y ordena por fecha ascendente (ISO yyyy-MM-dd)
        List<PublicHoliday> list = new ArrayList<>();
        if (rawHolidays != null) list.addAll(rawHolidays);
        System.out.println("LOG:"+rawHolidays.get(1).toString());
        Collections.sort(list, (h1, h2) -> {
            String d1 = h1.getDate() == null ? "" : h1.getDate();
            String d2 = h2.getDate() == null ? "" : h2.getDate();
            return d1.compareTo(d2);
        });

        if (!list.isEmpty()) {
            // Actualiza RV
            holidayList.clear();
            holidayList.addAll(list);
            holidayAdapter.setHolidayList(holidayList);
            binding.tvNoFestivos.setVisibility(View.GONE);
            //    binding.recyclerViewFestivos.setVisibility(View.VISIBLE);

            // Actualiza calendario: set de LocalDate con los festivos
            scopeByDate.clear();
            festivosSet.clear();

            for (PublicHoliday h : list) {
                String dateIso = h.getDate();
                if (dateIso == null || dateIso.length() != 10) continue;
                try {
                    java.time.LocalDate d = java.time.LocalDate.parse(dateIso);
                    String scope = normalizeScope(h.getScope()); // método de abajo
                    // Si ya hay uno para ese día, nos quedamos con el de mayor prioridad
                    String prev = scopeByDate.get(d);
                    if (prev == null || scopePriority(scope) > scopePriority(prev)) {
                        scopeByDate.put(d, scope);
                    }
                    festivosSet.add(d); // por si lo usas en otros sitios
                } catch (Exception ignored) {}
            }

            // Mueve calendario al año elegido y repinta
            int year = readYearOrCurrent();
            setupCalendarForYear(year);
            if (calView != null) calView.notifyCalendarChanged();

            // Muestra Card del calendario
            binding.cardCalendar.setVisibility(View.VISIBLE);

        } else {
            binding.tvNoFestivos.setText("No se encontraron festivos para " + selectedProvinciaName + ".");
            binding.tvNoFestivos.setVisibility(View.VISIBLE);
            // binding.recyclerViewFestivos.setVisibility(View.GONE);
            binding.cardCalendar.setVisibility(View.GONE);
            holidayAdapter.setHolidayList(new ArrayList<>());
            festivosSet.clear();
            if (calView != null) calView.notifyCalendarChanged();
        }
    }

    private void renderError(String message) {
        binding.tvNoFestivos.setText(message);
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        //  binding.recyclerViewFestivos.setVisibility(View.GONE);
        binding.cardCalendar.setVisibility(View.GONE);
        holidayAdapter.setHolidayList(new ArrayList<>());
        festivosSet.clear();
        if (calView != null) calView.notifyCalendarChanged();
    }

    // ------------------ Kizitonwose: binders y setup ------------------

    private void initCalendarDayBinder() {
        calView.setDayBinder(new MonthDayBinder<DayContainer>() {
            @NonNull
            @Override
            public DayContainer create(@NonNull View view) {
                return new DayContainer(view);
            }

            @Override
            public void bind(@NonNull DayContainer container, @NonNull CalendarDay day) {
                TextView tv = container.textView;
                tv.setText(String.valueOf(day.getDate().getDayOfMonth()));

                // Atenúa días fuera del mes
                tv.setAlpha(day.getPosition() == DayPosition.MonthDate ? 1f : 0.3f);

                String scope = scopeByDate.get(day.getDate());
                if (scope != null && day.getPosition() == DayPosition.MonthDate) {
                    // Fondo según tipo y texto en blanco para contraste
                    tv.setBackgroundResource(bgForScope(scope)); // método de abajo
                    tv.setTextColor(android.graphics.Color.WHITE);
                    tv.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    tv.setBackground(null);
                    tv.setTextColor(0xFF222222); // o el color por defecto de tu tema
                    tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                View root = container.getView();
                root.setOnClickListener(v -> {
                    if (day.getPosition() != com.kizitonwose.calendar.core.DayPosition.MonthDate) {
                        binding.cardHolidayInfo.setVisibility(View.GONE);
                    } else {
                        showHolidayPopup(root, day.getDate());
                    }
                });
            }
        });
    }

    private void showHolidayPopup(View anchor, LocalDate date) {
        List<PublicHoliday> dayHolidays = getHolidaysByDate(date);
        if (dayHolidays.isEmpty()) {
            dismissHolidayPopup();
            return;
        }

        // Cierra anterior si existe
        dismissHolidayPopup();

        // Infla layout del popup
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View content = inflater.inflate(
                com.example.lagvis_v1.R.layout.popup_holiday_info, null, false);

        TextView tvDate = content.findViewById(com.example.lagvis_v1.R.id.tvPopupDate);
        TextView tvProv = content.findViewById(com.example.lagvis_v1.R.id.tvPopupProvince);
        ChipGroup chips = content.findViewById(com.example.lagvis_v1.R.id.chipGroupPopup);

        // Fecha bonita capitalizada
        String niceDate = capitalizeFirst(date.format(DATE_FMT));
        tvDate.setText(niceDate);

        // Provincia/selector actual
        tvProv.setText(
                (selectedProvinciaName != null && !selectedProvinciaName.isEmpty())
                        ? selectedProvinciaName : "");

        // Chips por cada festivo
        for (PublicHoliday h : dayHolidays) {
            Chip chip = new Chip(requireContext());
            chip.setText(h.getName() != null ? h.getName() : "Festivo");
            chip.setTextColor(android.graphics.Color.WHITE);
            chip.setClickable(false);
            chip.setCheckable(false);

            chip.setSingleLine(true);
            chip.setEllipsize(android.text.TextUtils.TruncateAt.END);
            chip.setTextSize(15); // un poco mayor

            // ⬇️ Más separación texto-borde
            chip.setChipMinHeight(dp(36));
            chip.setChipStartPadding(dp(12));
            chip.setChipEndPadding(dp(12));
            chip.setTextStartPadding(dp(6));
            chip.setTextEndPadding(dp(6));
            chip.setChipCornerRadius(24f);

            chip.setEnsureMinTouchTargetSize(false); // mantenlo así para que el padding que pongas se respete

            chip.setChipBackgroundColorResource(colorForScope(h.getScope()));
            chips.addView(chip);
        }


        // Crea PopupWindow
        holidayPopup = new PopupWindow(content,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true /* focusable cierra con back */);

        holidayPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        holidayPopup.setOutsideTouchable(true);
        holidayPopup.setAnimationStyle(com.example.lagvis_v1.R.style.CalendarPopupAnim);

        // Calcula posición: centrado horizontal respecto al día y por encima (con margen)
        // Primero medimos el contenido:
        content.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int popupW = content.getMeasuredWidth();
        int popupH = content.getMeasuredHeight();

        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc); // x, y absolutos en pantalla del anchor
        int anchorW = anchor.getWidth();
        int anchorH = anchor.getHeight();

        // Coordenadas para showAtLocation (relativas a la pantalla)
        int x = loc[0] + (anchorW / 2) - (popupW / 2);
        int y = loc[1] - popupH - dp(8); // encima del día con 8dp de margen

        // Asegura que no se sale por los lados (opcional, simple clamp)
        if (x < dp(8)) x = dp(8);

        // Muestra
        holidayPopup.showAtLocation(anchor, Gravity.TOP | Gravity.START, x, y);
    }

    private void dismissHolidayPopup() {
        if (holidayPopup != null) {
            if (holidayPopup.isShowing()) holidayPopup.dismiss();
            holidayPopup = null;
        }
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(v * d);
    }



    private List<PublicHoliday> getHolidaysByDate(LocalDate date) {
        List<PublicHoliday> result = new ArrayList<>();
        for (PublicHoliday h : holidayList) {
            String di = h.getDate();
            if (di != null && di.length() == 10) {
                try {
                    if (LocalDate.parse(di).equals(date)) result.add(h);
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    private void showHolidayCard(LocalDate date) {
        List<PublicHoliday> dayHolidays = getHolidaysByDate(date);
        if (dayHolidays.isEmpty()) {
            binding.cardHolidayInfo.setVisibility(View.GONE);
            return;
        }

        // Cabecera
        String niceDate = capitalizeFirst(date.format(DATE_FMT));
        binding.tvHolidayDate.setText(niceDate);

        // Provincia (si la tienes del selector)
        binding.tvHolidayProvince.setText(
                selectedProvinciaName != null && !selectedProvinciaName.isEmpty()
                        ? selectedProvinciaName
                        : ""
        );

        // Chips
        ChipGroup group = binding.chipGroupHolidays;
        group.removeAllViews();

        for (PublicHoliday h : dayHolidays) {
            Chip chip = new Chip(requireContext());
            chip.setText(h.getName() != null ? h.getName() : "Festivo");
            chip.setTextColor(android.graphics.Color.WHITE);
            chip.setChipMinHeight(0);
            chip.setTextSize(14);
            chip.setClickable(false);
            chip.setCheckable(false);

            chip.setChipBackgroundColorResource(colorForScope(h.getScope()));

            // ✅ Solo una línea, con elipsis
            chip.setSingleLine(true); // (opcional, ya es el default)
            chip.setEllipsize(android.text.TextUtils.TruncateAt.END);

            // (opcional) evitar que Material aumente el alto mínimo por accesibilidad
            chip.setEnsureMinTouchTargetSize(false);

            // Si no te convence el radio:
            chip.setChipCornerRadius(24f);

            // ❌ NO LLAMAR:
            // chip.setMaxLines(2); // <-- esta línea causaba el crash

            group.addView(chip);
        }


        // Mostrar tarjeta
        binding.cardHolidayInfo.setVisibility(View.VISIBLE);
    }

    private int colorForScope(String raw) {
        String scope = normalizeScope(raw);
        switch (scope) {
            case "nacional":   return com.example.lagvis_v1.R.color.festivo_nacional;
            case "autonomico": return com.example.lagvis_v1.R.color.festivo_autonomico;
            case "municipal":  return com.example.lagvis_v1.R.color.festivo_municipal;
            case "local":      return com.example.lagvis_v1.R.color.festivo_local;
            case "info":
            default:           return com.example.lagvis_v1.R.color.festivo_info;
        }
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase(ES) + s.substring(1);
    }



    private String normalizeScope(String s) {
        if (s == null) return "otros";
        String t = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(java.util.Locale.ROOT)
                .trim();
        if (t.equals("estatal")) return "nacional";
        if (t.startsWith("autonom")) return "autonomico"; // autonomico/autonomica
        if (t.startsWith("municip")) return "municipal";   // municipal/municipio
        if (t.startsWith("local") || t.startsWith("provinc")) return "local";
        if (t.startsWith("info")) return "info";
        if (t.equals("nacional") || t.equals("autonomico") || t.equals("municipal") || t.equals("local") || t.equals("info"))
            return t;
        return "otros";
    }

    private int scopePriority(String scope) {
        if (scope == null) return 0;
        switch (scope) {
            case "nacional":
                return 5;
            case "autonomico":
                return 4;
            case "municipal":
                return 3;
            case "local":
                return 2;
            case "info":
                return 1;
            default:
                return 0; // otros
        }
    }

    @androidx.annotation.DrawableRes
    private int bgForScope(String scope) {
        if (scope == null) return com.example.lagvis_v1.R.drawable.bg_festivo_info;
        switch (scope) {
            case "nacional":
                return com.example.lagvis_v1.R.drawable.bg_festivo_nacional;
            case "autonomico":
                return com.example.lagvis_v1.R.drawable.bg_festivo_autonomico;
            case "municipal":
                return com.example.lagvis_v1.R.drawable.bg_festivo_municipal;
            case "local":
                return com.example.lagvis_v1.R.drawable.bg_festivo_local;
            case "info":
            default:
                return com.example.lagvis_v1.R.drawable.bg_festivo_info;
        }
    }


    private void setupCalendarForYear(int year) {
        if (calendarYearShown == year) return;
        YearMonth start = YearMonth.of(year, 1);
        YearMonth end = YearMonth.of(year, 12);
        DayOfWeek firstDay = DayOfWeek.MONDAY;

        calView.setup(start, end, firstDay);
        calView.scrollToMonth(start);

        calendarYearShown = year;
    }

    // Contenedor para la celda del día (usa el layout @layout/calendar_day_layout)
    public static class DayContainer extends ViewContainer {
        public final TextView textView;

        public DayContainer(@NonNull View view) {
            super(view);
            // OBLIGATORIO: tomar el TextView del layout de día
            textView = view.findViewById(com.example.lagvis_v1.R.id.calendarDayText);
        }
    }

    public static class MonthHeaderContainer extends ViewContainer {
        public final TextView title;

        public MonthHeaderContainer(@NonNull View view) {
            super(view);
            // Debe existir un TextView con este id en tu layout de cabecera
            title = view.findViewById(com.example.lagvis_v1.R.id.tvMonthTitle);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // evitar fugas de memoria
    }
}
