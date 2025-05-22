package FiniquitosPackage;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivityDatosGeneralesDespido extends AppCompatActivity {

    private EditText etFechaInicio, etFechaFin, etSalarioDiario;
    private Button btnCalcular;

    // Constantes para las claves de los datos que pasaremos en el Intent
    public static final String EXTRA_SALARIO_DIARIO = "com.example.lagvis_v1.EXTRA_SALARIO_DIARIO";
    public static final String EXTRA_MESES_TRABAJADOS = "com.example.lagvis_v1.EXTRA_MESES_TRABAJADOS";
    public static final String EXTRA_DIAS_TRABAJADOS = "com.example.lagvis_v1.EXTRA_DIAS_TRABAJADOS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_generales_despido);

        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        etSalarioDiario = findViewById(R.id.etSalarioDiario);
        btnCalcular = findViewById(R.id.btnCalcular);

        setupDatePicker(etFechaInicio);
        setupDatePicker(etFechaFin);

        btnCalcular.setOnClickListener(v -> procesarDatosYNavegar());
    }

    private void setupDatePicker(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityDatosGeneralesDespido.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
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
                Toast.makeText(this, "Por favor, introduce ambas fechas.", Toast.LENGTH_SHORT).show();
                return;
            }

            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            if (fechaFin.before(fechaInicio)) {
                Toast.makeText(this, "La fecha de fin no puede ser anterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                return;
            }

            String salarioDiarioStr = etSalarioDiario.getText().toString();
            if (salarioDiarioStr.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce el salario diario.", Toast.LENGTH_SHORT).show();
                return;
            }
            double salarioDiario = Double.parseDouble(salarioDiarioStr);

            long diffInMillies = Math.abs(fechaFin.getTime() - fechaInicio.getTime());
            long diasTrabajados = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            int mesesTrabajados = (int) (diasTrabajados / 30); // Aproximación

            // Crear el Intent para iniciar ActivityResultadosDespido
            Intent intent = new Intent(ActivityDatosGeneralesDespido.this, ActivityResultadoDespido.class);

            // Poner los datos en el Intent como extras
            intent.putExtra(EXTRA_SALARIO_DIARIO, salarioDiario);
            intent.putExtra(EXTRA_MESES_TRABAJADOS, mesesTrabajados);
            intent.putExtra(EXTRA_DIAS_TRABAJADOS, diasTrabajados); // También pasamos los días por si acaso
            intent.putExtra("fechaInicioFormatted", fechaInicioStr); // AÑADIR ESTA LINEA
            intent.putExtra("fechaFinFormatted", fechaFinStr);
            // Iniciar la nueva Activity
            startActivity(intent);

        } catch (ParseException e) {
            Toast.makeText(this, "Error en el formato de fecha. Usa dd/MM/yyyy", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error en el formato del salario diario. Introduce un número válido.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Ocurrió un error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}