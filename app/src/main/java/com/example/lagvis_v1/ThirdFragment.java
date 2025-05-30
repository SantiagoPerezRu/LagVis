package com.example.lagvis_v1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import FiniquitosPackage.ActivityDatosGeneralesFiniquito;

public class ThirdFragment extends BaseFragment {

    private EditText etFechaContrato;
    private EditText etFechaDespido;
    private Button btnSiguiente;
    private Calendar calendarContrato;
    private Calendar calendarDespido;
    private SimpleDateFormat dateFormatter;

    private Drawable errorIcon;
    private Drawable checkIcon;

    public ThirdFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
        checkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_circle);

        // Inicializar las vistas
        etFechaContrato = view.findViewById(R.id.etFechaContrato);
        etFechaDespido = view.findViewById(R.id.etFechaDespido);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);

        // Inicializar los calendarios y el formato de fecha
        calendarContrato = Calendar.getInstance();
        calendarDespido = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Configurar OnClickListener para el EditText de fecha de contrato
        etFechaContrato.setOnClickListener(v -> mostrarDatePickerDialog(calendarContrato, etFechaContrato));

        // Configurar OnClickListener para el EditText de fecha de despido
        etFechaDespido.setOnClickListener(v -> mostrarDatePickerDialog(calendarDespido, etFechaDespido));

        // Configurar OnClickListener para el botón Siguiente
        btnSiguiente.setOnClickListener(v -> calcularYEnviarDiasTrabajados());

        return view;
    }

    private void mostrarDatePickerDialog(Calendar calendar, final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    editText.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void calcularYEnviarDiasTrabajados() {
        String fechaContratoStr = etFechaContrato.getText().toString();
        String fechaDespidoStr = etFechaDespido.getText().toString();

        if (fechaContratoStr.isEmpty() || fechaDespidoStr.isEmpty()) {
            //Toast.makeText(requireContext(), "Por favor, selecciona ambas fechas.", Toast.LENGTH_SHORT).show();
            mostrarToastPersonalizado("Por favor, selecciona ambas fechas", errorIcon);
            return;
        }

        try {
            Date fechaContrato = dateFormatter.parse(fechaContratoStr);
            Date fechaDespido = dateFormatter.parse(fechaDespidoStr);

            if (fechaContrato != null && fechaDespido != null && fechaDespido.before(fechaContrato)) {
                //Toast.makeText(requireContext(), "La fecha de despido no puede ser anterior a la fecha de contrato.", Toast.LENGTH_SHORT).show();
                mostrarToastPersonalizado("La fecha de despido no puede ser anterior a la fecha de contrato.", errorIcon);
                return;
            }

            long diferenciaMillis = fechaDespido.getTime() - fechaContrato.getTime();
            long diasTrabajados = TimeUnit.DAYS.convert(diferenciaMillis, TimeUnit.MILLISECONDS);

            // Crear un Intent para la siguiente actividad
            Intent intent = new Intent(requireContext(), ActivityDatosGeneralesFiniquito.class);
            intent.putExtra("diasTrabajados", diasTrabajados);
            startActivity(intent);

        } catch (ParseException e) {
            mostrarToastPersonalizado("Error al parsear las fechas.", errorIcon);
            e.printStackTrace();
        }
    }
}