// com/example/lagvis_v1/ui/auth/AdvancedFormRegister.java
package com.example.lagvis_v1.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseActivity;
import com.example.lagvis_v1.databinding.ActivityAdvancedFormRegisterBinding;

import java.util.Calendar;

public class AdvancedFormRegister extends BaseActivity {

    private ActivityAdvancedFormRegisterBinding binding;

    private AdvancedFormViewModel vm;
    private AuthViewModel authVm;

    private String fechaNacimiento; // dd/MM/yyyy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdvancedFormRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ViewModels
        vm = new ViewModelProvider(this, new AdvancedFormViewModelFactory())
                .get(AdvancedFormViewModel.class);
        authVm = new ViewModelProvider(this, new AuthViewModelFactory())
                .get(AuthViewModel.class);

        // Autocomplete adapters
        String[] comunidades = getResources().getStringArray(R.array.comunidades_autonomas_registro);
        String[] sectores = getResources().getStringArray(R.array.sectores);

        binding.autoCompleteTextViewComunidadAutonoma.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, comunidades)
        );
        binding.autoCompleteTextViewSectores.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sectores)
        );

        // Fecha
        binding.btnFecha.setOnClickListener(v -> mostrarDatePicker());

        // Enviar
        binding.btnEnviar.setOnClickListener(v -> registrarNuevoUsuario());

        // Observa envío
        vm.submit.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.btnEnviar.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.btnEnviar.setEnabled(true);
                Drawable ok = getDrawable(R.drawable.ic_check_circle);
                showCustomToast("Datos registrados correctamente", ok);
                startActivity(new Intent(AdvancedFormRegister.this, LoginActivity.class));
                finish();
            } else if (state instanceof UiState.Error) {
                binding.btnEnviar.setEnabled(true);
                Drawable err = getDrawable(R.drawable.ic_error_outline);
                String msg = ((UiState.Error<?>) state).message;
                showCustomToast(msg != null ? msg : "Error en el registro", err);
            }
        });
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);

        DatePickerDialog dp = new DatePickerDialog(
                AdvancedFormRegister.this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    Calendar seleccionada = Calendar.getInstance();
                    seleccionada.set(year, month, dayOfMonth);
                    if (seleccionada.after(Calendar.getInstance())) {
                        Drawable err = getDrawable(R.drawable.ic_error_outline);
                        showCustomToast("La fecha no puede ser futura!", err);
                    } else {
                        fechaNacimiento = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.btnFecha.setText(fechaNacimiento);
                    }
                },
                ano, mes, dia
        );
        dp.show();
    }

    private void registrarNuevoUsuario() {
        // Validaciones de UI
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) {
            Drawable err = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("¡Debe seleccionar una fecha de nacimiento!", err);
            return;
        }

        String nombre   = binding.nombreEditText.getText().toString().trim();
        String ape1     = binding.apellidoEditText.getText().toString().trim();
        String ape2     = binding.apellido2EditText.getText().toString().trim();
        String comunidad= binding.autoCompleteTextViewComunidadAutonoma.getText().toString().trim();
        String sector   = binding.autoCompleteTextViewSectores.getText().toString().trim();

        if (nombre.isEmpty() || ape1.isEmpty() || ape2.isEmpty() ||
                comunidad.isEmpty() || sector.isEmpty()) {
            Drawable err = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("Debe introducir todos los campos!", err);
            return;
        }

        // Validación fecha (no futura)
        try {
            String[] partes = fechaNacimiento.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]) - 1;
            int ano = Integer.parseInt(partes[2]);

            Calendar sel = Calendar.getInstance();
            sel.set(ano, mes, dia);
            if (sel.after(Calendar.getInstance())) {
                Drawable err = getDrawable(R.drawable.ic_error_outline);
                showCustomToast("¡La fecha de nacimiento no puede ser futura!", err);
                return;
            }
        } catch (Exception e) {
            Drawable err = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("¡Error al procesar la fecha de nacimiento!", err);
            return;
        }

        String uid = authVm.uidOrNull();
        if (uid == null) {
            Drawable err = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("¡Usuario no autenticado!", err);
            return;
        }

        String comunidadId = String.valueOf(obtenerIdComunidadAutonoma(comunidad));
        String sectorId    = String.valueOf(com.example.lagvis_v1.core.util.LagVisConstantes.getSectorId(sector));

        // Enviar por VM (MVVM)
        vm.send(uid, nombre, ape1, ape2, comunidadId, sectorId, fechaNacimiento);
    }

    // Helpers (puedes moverlos a util si quieres)
    public int obtenerIdComunidadAutonoma(@NonNull String comunidadAutonoma) {
        switch (comunidadAutonoma) {
            case "Andalucía": return 1;
            case "Aragón": return 2;
            case "Asturias": return 3;
            case "Cantabria": return 4;
            case "Castilla-La Mancha": return 5;
            case "Castilla y León": return 6;
            case "Cataluña": return 7;
            case "Comunidad Valenciana": return 8;
            case "Extremadura": return 9;
            case "Galicia": return 10;
            case "Illes Balears": return 11;
            case "Canarias": return 12;
            case "La Rioja": return 13;
            case "Comunidad de Madrid": return 14;
            case "Región de Murcia": return 15;
            case "Navarra": return 16;
            case "País Vasco": return 17;
            default: return -1;
        }
    }
}
