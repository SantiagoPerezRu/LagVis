package com.example.lagvis_v1.ui.finiquitos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lagvis_v1.databinding.FragmentThirdBinding;

import java.util.Calendar;
import java.util.Locale;

public class CalculadoraFiniquitosFragment extends Fragment {

    private FragmentThirdBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configura los date pickers en los EditText del layout "third"
        setupDatePicker(binding.etFechaContrato);
        setupDatePicker(binding.etFechaDespido);

        // Botón siguiente: lee las fechas introducidas aquí y lanza la Activity
        binding.btnSiguiente.setOnClickListener(v -> {
            String fechaInicio = text(binding.etFechaContrato);
            String fechaFin    = text(binding.etFechaDespido);

            if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona ambas fechas", Toast.LENGTH_LONG).show();
                return;
            }

           // Intent i = new Intent(requireContext(), ActivityDatosGeneralesFiniquito.class);
          //  i.putExtra("fechaInicio", fechaInicio);
          //  i.putExtra("fechaFin",    fechaFin);
          //  startActivity(i);
        });
    }

    private void setupDatePicker(@NonNull final EditText editText) {
        // Evita teclado y abre calendario tanto al click como al ganar foco
        editText.setInputType(InputType.TYPE_NULL);
        editText.setKeyListener(null);
        editText.setOnClickListener(v -> showCalendar(editText));
        editText.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showCalendar(editText); });
    }

    private void showCalendar(@NonNull final EditText target) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int y, int m, int d) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                    target.setText(formatted);
                    target.clearFocus();
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dlg.show();
    }

    private String text(EditText e) {
        CharSequence cs = e.getText();
        return cs == null ? "" : cs.toString().trim();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
