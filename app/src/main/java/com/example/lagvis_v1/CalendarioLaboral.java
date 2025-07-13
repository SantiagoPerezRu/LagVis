package com.example.lagvis_v1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log; // Para depuración en caso de errores
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections; // Para ordenar la lista de festivos
import java.util.Set;     // Importa Set
import java.util.HashSet;   // Importa HashSet


import api.NagerDateApi; // Tus imports existentes
import api.PublicHoliday; // Tus imports existentes

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarioLaboral extends Fragment {

    private AutoCompleteTextView autoCompleteTextViewComunidades;
    private Button btnEnviar;
    private TextView tvNoFestivos;
    private RecyclerView recyclerViewFestivos;
    private HolidayAdapter holidayAdapter;
    private List<PublicHoliday> holidayList = new ArrayList<>();

    private final Map<String, String> comunidadesAutonomasApiCodes = new HashMap<>();

    public CalendarioLaboral() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComunidadesAutonomasApiCodes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_laboral, container, false);

        autoCompleteTextViewComunidades = view.findViewById(R.id.autoCompleteTextViewComunidades);
        btnEnviar = view.findViewById(R.id.btnEnviar);

        recyclerViewFestivos = view.findViewById(R.id.recyclerViewFestivos);
        tvNoFestivos = view.findViewById(R.id.tvNoFestivos);
        recyclerViewFestivos.setLayoutManager(new LinearLayoutManager(getContext()));
        holidayAdapter = new HolidayAdapter(holidayList);
        recyclerViewFestivos.setAdapter(holidayAdapter);

        String[] comunidadesArray = getResources().getStringArray(R.array.comunidades_autonomas);
        ArrayAdapter<String> arrAdapterComunidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, comunidadesArray);
        autoCompleteTextViewComunidades.setAdapter(arrAdapterComunidades);

        autoCompleteTextViewComunidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        btnEnviar.setOnClickListener(v -> {
            String selectedComunidad = autoCompleteTextViewComunidades.getText().toString();
            String apiCountyCode = comunidadesAutonomasApiCodes.get(selectedComunidad);

            if (apiCountyCode != null) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                fetchPublicHolidays(currentYear, "ES", apiCountyCode, selectedComunidad);
            } else {
                Toast.makeText(getContext(), "Por favor, selecciona una comunidad válida de la lista.", Toast.LENGTH_SHORT).show();
                tvNoFestivos.setText("Selecciona una comunidad válida para ver los festivos.");
                tvNoFestivos.setVisibility(View.VISIBLE);
                recyclerViewFestivos.setVisibility(View.GONE);
                holidayAdapter.setHolidayList(new ArrayList<>());
            }
        });

        return view;
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

    private void fetchPublicHolidays(int year, String countryCode, String countyCode, String comunidadNombre) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://date.nager.at/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        NagerDateApi service = retrofit.create(NagerDateApi.class);

        tvNoFestivos.setText("Cargando festivos para " + comunidadNombre + "...");
        tvNoFestivos.setVisibility(View.VISIBLE);
        recyclerViewFestivos.setVisibility(View.GONE);

        service.getPublicHolidays(year, countryCode, countyCode).enqueue(new Callback<List<PublicHoliday>>() {
            @Override
            public void onResponse(Call<List<PublicHoliday>> call, Response<List<PublicHoliday>> response) {
                if (response.isSuccessful()) {
                    List<PublicHoliday> rawHolidays = response.body();
                    List<PublicHoliday> filteredHolidays = new ArrayList<>();
                    Set<PublicHoliday> uniqueHolidays = new HashSet<>();

                    if (rawHolidays != null && !rawHolidays.isEmpty()) {
                        for (PublicHoliday holiday : rawHolidays) {
                            boolean isSpecificToCounty = (holiday.getCounties() != null && holiday.getCounties().contains(countyCode));
                            if (holiday.isGlobal() || isSpecificToCounty) {
                                uniqueHolidays.add(holiday);
                            }
                        }

                        filteredHolidays.addAll(uniqueHolidays);
                        Collections.sort(filteredHolidays, (h1, h2) -> h1.getDate().compareTo(h2.getDate())); // Ordena por fecha
                    }

                    if (!filteredHolidays.isEmpty()) {
                        holidayList.clear();
                        holidayList.addAll(filteredHolidays);
                        holidayAdapter.setHolidayList(holidayList);

                        tvNoFestivos.setVisibility(View.GONE);
                        recyclerViewFestivos.setVisibility(View.VISIBLE);
                    } else {
                        tvNoFestivos.setText("No se encontraron festivos para " + comunidadNombre + " en " + year + ".");
                        tvNoFestivos.setVisibility(View.VISIBLE);
                        recyclerViewFestivos.setVisibility(View.GONE);
                        holidayAdapter.setHolidayList(new ArrayList<>());
                    }
                } else {
                    Log.e("CalendarioLaboral", "Error al obtener festivos: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Error al obtener los festivos: " + response.code(), Toast.LENGTH_LONG).show();
                    tvNoFestivos.setText("Error al cargar los festivos. Código: " + response.code());
                    tvNoFestivos.setVisibility(View.VISIBLE);
                    recyclerViewFestivos.setVisibility(View.GONE);
                    holidayAdapter.setHolidayList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<PublicHoliday>> call, Throwable t) {
                Log.e("CalendarioLaboral", "Fallo de red: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvNoFestivos.setText("Error de conexión al cargar los festivos.");
                tvNoFestivos.setVisibility(View.VISIBLE);
                recyclerViewFestivos.setVisibility(View.GONE);
                holidayAdapter.setHolidayList(new ArrayList<>());
            }
        });
    }
}