package com.example.lagvis_v1.ui.calendario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.databinding.FragmentCalendarioLaboralBinding;
import com.example.lagvis_v1.dominio.PublicHoliday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarioLaboral extends Fragment {

    private FragmentCalendarioLaboralBinding binding;   // ViewBinding
    private HolidayAdapter holidayAdapter;
    private final List<PublicHoliday> holidayList = new ArrayList<>();
    private final Map<String, String> comunidadesAutonomasApiCodes = new HashMap<>();

    private HolidaysViewModel vm;

    // Estado UI auxiliar
    private String selectedComunidadName = "";
    private String selectedCountyCode = "";

    public CalendarioLaboral() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComunidadesAutonomasApiCodes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarioLaboralBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView
        binding.recyclerViewFestivos.setLayoutManager(new LinearLayoutManager(requireContext()));
        holidayAdapter = new HolidayAdapter(holidayList);
        binding.recyclerViewFestivos.setAdapter(holidayAdapter);

        // Autocomplete con comunidades
        String[] comunidadesArray = getResources().getStringArray(R.array.comunidades_autonomas);
        ArrayAdapter<String> arrAdapterComunidades = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                comunidadesArray
        );
        binding.autoCompleteTextViewComunidades.setAdapter(arrAdapterComunidades);
        binding.autoCompleteTextViewComunidades.setOnItemClickListener((adapterView, v, i, l) -> {
            selectedComunidadName = binding.autoCompleteTextViewComunidades.getText().toString();
            selectedCountyCode = comunidadesAutonomasApiCodes.getOrDefault(selectedComunidadName, "");
        });

        // ViewModel
        vm = new ViewModelProvider(this, new HolidaysViewModelFactory())
                .get(HolidaysViewModel.class);

        // Observa estado del VM
        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                showLoading("Cargando festivos" + (selectedComunidadName.isEmpty() ? "…" : " para " + selectedComunidadName + "…"));
            } else if (state instanceof UiState.Success) {
                List<PublicHoliday> raw = ((UiState.Success<List<PublicHoliday>>) state).data;
                renderSuccess(raw);
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).message;
                renderError("Error al cargar los festivos" + (msg != null ? (": " + msg) : "."));
            }
        });

        // Botón buscar
        binding.btnEnviar.setOnClickListener(v -> {
            String selected = binding.autoCompleteTextViewComunidades.getText().toString();
            String code = comunidadesAutonomasApiCodes.get(selected);
            selectedComunidadName = selected;
            selectedCountyCode = code != null ? code : "";

            if (code != null) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                // Dispara carga MVVM
                vm.load(currentYear, "ES");
            } else {
                Toast.makeText(getContext(), "Por favor, selecciona una comunidad válida de la lista.", Toast.LENGTH_SHORT).show();
                binding.tvNoFestivos.setText("Selecciona una comunidad válida para ver los festivos.");
                binding.tvNoFestivos.setVisibility(View.VISIBLE);
                binding.recyclerViewFestivos.setVisibility(View.GONE);
                holidayAdapter.setHolidayList(new ArrayList<>());
            }
        });

        // Estado inicial
        binding.tvNoFestivos.setText("Selecciona una comunidad y pulsa Buscar.");
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        binding.recyclerViewFestivos.setVisibility(View.GONE);
    }

    private void showLoading(String message) {
        binding.tvNoFestivos.setText(message);
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        binding.recyclerViewFestivos.setVisibility(View.GONE);
    }

    private void renderSuccess(List<PublicHoliday> rawHolidays) {
        // Filtra por global o específico de la comunidad seleccionada (countyCode)
        List<PublicHoliday> filtered = new ArrayList<>();
        Set<PublicHoliday> unique = new HashSet<>();

        if (rawHolidays != null && !rawHolidays.isEmpty()) {
            for (PublicHoliday h : rawHolidays) {
                boolean isSpecificToCounty = (h.getCounties() != null && selectedCountyCode != null
                        && h.getCounties().contains(selectedCountyCode));
                if (h.isGlobal() || isSpecificToCounty) {
                    unique.add(h);
                }
            }
            filtered.addAll(unique);
            // Ordena por fecha ascendente (asumiendo getDate() es comparable tipo String ISO yyyy-MM-dd)
            Collections.sort(filtered, (h1, h2) -> h1.getDate().compareTo(h2.getDate()));
        }

        if (!filtered.isEmpty()) {
            holidayList.clear();
            holidayList.addAll(filtered);
            holidayAdapter.setHolidayList(holidayList);
            binding.tvNoFestivos.setVisibility(View.GONE);
            binding.recyclerViewFestivos.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoFestivos.setText("No se encontraron festivos para " + selectedComunidadName + ".");
            binding.tvNoFestivos.setVisibility(View.VISIBLE);
            binding.recyclerViewFestivos.setVisibility(View.GONE);
            holidayAdapter.setHolidayList(new ArrayList<>());
        }
    }

    private void renderError(String message) {
        binding.tvNoFestivos.setText(message);
        binding.tvNoFestivos.setVisibility(View.VISIBLE);
        binding.recyclerViewFestivos.setVisibility(View.GONE);
        holidayAdapter.setHolidayList(new ArrayList<>());
    }

    private void setupComunidadesAutonomasApiCodes() {
        comunidadesAutonomasApiCodes.put("Andalucía", "ES-AN");
        comunidadesAutonomasApiCodes.put("Aragón", "ES-AR");
        comunidadesAutonomasApiCodes.put("Principado de Asturias", "ES-AS");
        comunidadesAutonomasApiCodes.put("Cantabria", "ES-CB");
        comunidadesAutonomasApiCodes.put("Castilla y León", "ES-CL");
        comunidadesAutonomasApiCodes.put("Castilla-La Mancha", "ES-CM");
        comunidadesAutonomasApiCodes.put("Cataluña", "ES-CT");
        comunidadesAutonomasApiCodes.put("Comunidad de Madrid", "ES-MD");
        comunidadesAutonomasApiCodes.put("Comunidad Valenciana", "ES-VC");
        comunidadesAutonomasApiCodes.put("Extremadura", "ES-EX");
        comunidadesAutonomasApiCodes.put("Galicia", "ES-GA");
        comunidadesAutonomasApiCodes.put("Islas Baleares", "ES-PM");
        comunidadesAutonomasApiCodes.put("Islas Canarias", "ES-CN");
        comunidadesAutonomasApiCodes.put("La Rioja", "ES-RI");
        comunidadesAutonomasApiCodes.put("Región de Murcia", "ES-MC");
        comunidadesAutonomasApiCodes.put("Comunidad Foral de Navarra", "ES-NC");
        comunidadesAutonomasApiCodes.put("País Vasco", "ES-PV");
        comunidadesAutonomasApiCodes.put("Ceuta", "ES-CE");
        comunidadesAutonomasApiCodes.put("Melilla", "ES-ML");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // ViewBinding: evita fugas de memoria
    }
}
