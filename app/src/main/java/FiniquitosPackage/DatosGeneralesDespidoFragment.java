package FiniquitosPackage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lagvis_v1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatosGeneralesDespidoFragment extends Fragment {

    private EditText etFechaInicio, etFechaFin, etSalarioDiario;
    private Button btnCalcular;

    public static final String EXTRA_SALARIO_DIARIO = "com.example.lagvis_v1.EXTRA_SALARIO_DIARIO";
    public static final String EXTRA_MESES_TRABAJADOS = "com.example.lagvis_v1.EXTRA_MESES_TRABAJADOS";
    public static final String EXTRA_DIAS_TRABAJADOS = "com.example.lagvis_v1.EXTRA_DIAS_TRABAJADOS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datos_generales_despido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        etSalarioDiario = view.findViewById(R.id.etSalarioDiario);
        btnCalcular = view.findViewById(R.id.btnCalcular);

        crearCalendario(etFechaInicio);
        crearCalendario(etFechaFin);

        btnCalcular.setOnClickListener(v -> procesarDatosYNavegar());
    }

    private void crearCalendario(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        editText.setText(formattedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void procesarDatosYNavegar() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            String fechaInicioStr = etFechaInicio.getText().toString();
            String fechaFinStr = etFechaFin.getText().toString();

            if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, introduce ambas fechas.", Toast.LENGTH_SHORT).show();
                return;
            }

            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            if (fechaFin.before(fechaInicio)) {
                Toast.makeText(getContext(), "La fecha de fin no puede ser anterior a la de inicio.", Toast.LENGTH_LONG).show();
                return;
            }

            String salarioDiarioStr = etSalarioDiario.getText().toString();
            if (salarioDiarioStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, introduce el salario diario.", Toast.LENGTH_SHORT).show();
                return;
            }

            double salarioDiario = Double.parseDouble(salarioDiarioStr);

            long diffInMillies = Math.abs(fechaFin.getTime() - fechaInicio.getTime());
            long diasTrabajados = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            int mesesTrabajados = (int) (diasTrabajados / 30);

            // Aquí sí lanzamos el intent directamente desde el fragment
            Intent intent = new Intent(requireContext(), ActivityResultadoDespido.class);
            intent.putExtra(EXTRA_SALARIO_DIARIO, salarioDiario);
            intent.putExtra(EXTRA_MESES_TRABAJADOS, mesesTrabajados);
            intent.putExtra(EXTRA_DIAS_TRABAJADOS, diasTrabajados);
            intent.putExtra("fechaInicioFormatted", fechaInicioStr);
            intent.putExtra("fechaFinFormatted", fechaFinStr);
            startActivity(intent);

        } catch (ParseException | NumberFormatException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
