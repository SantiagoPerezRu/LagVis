package FiniquitosPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R;

public class ActivityDatosGeneralesFiniquito extends AppCompatActivity {

    private EditText etSalarioAnual;
    private EditText etDiasVacaciones;
    private Spinner spinnerPagas;
    private Spinner spinnerTipoDespido;
    private Button btnCalcularFiniquito;
    private long diasTrabajados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_generales_finiquito);

        // Inicializar vistas
        etSalarioAnual = findViewById(R.id.etSalarioAnual);
        etDiasVacaciones = findViewById(R.id.etDiasVacaciones);
        spinnerPagas = findViewById(R.id.spinnerPagas);
        spinnerTipoDespido = findViewById(R.id.spinnerTipoDespido);
        btnCalcularFiniquito = findViewById(R.id.btnCalcularFiniquito);

        // Obtener los días trabajados del Intent
        diasTrabajados = getIntent().getLongExtra("diasTrabajados", 0);

        // Configurar Spinners
        configurarSpinnerPagas();
        configurarSpinnerTipoDespido();

        // Configurar OnClickListener para el botón Calcular Finiquito
        btnCalcularFiniquito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularFiniquito();
            }
        });
    }

    private void configurarSpinnerPagas() {
        // Opciones para el número de pagas
        String[] opcionesPagas = {"12 pagas", "14 pagas", "Pagas prorrateadas"};
        // Crear un ArrayAdapter usando un layout por defecto del sistema
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesPagas);
        // Especificar el layout a usar cuando se despliega la lista de opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asignar el adapter al spinner
        spinnerPagas.setAdapter(adapter);
    }

    private void configurarSpinnerTipoDespido() {
        String[] opcionesTipoDespido = {
                "Disciplinario (procedente)", // posición 0
                "Objetivo",                   // posición 1
                "Improcedente",              // posición 2
                "Nulo"                        // posición 3
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesTipoDespido);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDespido.setAdapter(adapter);
    }


    private void calcularFiniquito() {
        // Obtener los valores introducidos por el usuario
        String salarioAnualStr = etSalarioAnual.getText().toString().trim();
        String diasVacacionesStr = etDiasVacaciones.getText().toString().trim();
        int posicionPagas = spinnerPagas.getSelectedItemPosition();
        int posicionTipoDespido = spinnerTipoDespido.getSelectedItemPosition();

        if (salarioAnualStr.isEmpty() || diasVacacionesStr.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el salario anual y los días de vacaciones.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double salarioAnual = Double.parseDouble(salarioAnualStr);
            int diasVacaciones = Integer.parseInt(diasVacacionesStr);

            double salarioDiario = salarioAnual / 365.0;
            double salarioMensual = salarioAnual / 12.0;

            // 1. Salario correspondiente a días trabajados (si no se ha cobrado el mes actual)
            // Asumimos que si se trabaja el mes completo y el despido es al final, ya está cobrado.
            // Para un cálculo más preciso, necesitaríamos la fecha de despido exacta para calcular
            // la parte proporcional del mes si el despido no es a final de mes.
            // Para este ejemplo, simplificaremos y pondremos a 0 si se trabajó el año completo.
            double salarioPorDiasTrabajados = 0;
            if (diasTrabajados < 365) {
                // Esto es una simplificación. Lo ideal sería tener la fecha de inicio del mes de despido.
                // Aquí asumimos que los días trabajados en el año son los días trabajados en el mes actual.
                salarioPorDiasTrabajados = (salarioMensual / 30.0) * (diasTrabajados % 30); // Aproximación a días del mes
                if (salarioPorDiasTrabajados < 0) salarioPorDiasTrabajados = 0; // Evitar valores negativos si diasTrabajados es múltiplo de 30
            }


            // 2. Vacaciones no disfrutadas
            double importeVacaciones = diasVacaciones * salarioDiario;

            // 3. Pagas extra prorrateadas (solo si hay más de 12 pagas)
            double pagasExtra = 0;
            if (posicionPagas == 1) { // 14 pagas
                double pagaExtraAnual = salarioAnual / 14.0;
                // Prorrateo de la parte no devengada de las pagas extra.
                // Asumimos que las pagas extra son semestrales.
                // Esto es una simplificación y podría necesitar más detalle según el convenio.
                pagasExtra = (pagaExtraAnual * (diasTrabajados % 182.5)) / 182.5; // Aproximación a semestre en días
                if (pagasExtra < 0) pagasExtra = 0;
            } else if (posicionPagas == 2) { // Pagas prorrateadas
                pagasExtra = 0; // Si están prorrateadas, ya están incluidas en el salario anual.
            }

            // 4. Indemnización según el tipo de despido
            double indemnizacion = 0;
            double aniosTrabajados = diasTrabajados / 365.0;

            switch (posicionTipoDespido) {
                case 1: // Objetivo
                    indemnizacion = salarioDiario * 20 * aniosTrabajados;
                    double maxObjetivo = salarioMensual * 12;
                    indemnizacion = Math.min(indemnizacion, maxObjetivo);
                    break;
                case 2: // Improcedente
                    indemnizacion = salarioDiario * 33 * aniosTrabajados;
                    double maxImprocedente = salarioMensual * 24;
                    indemnizacion = Math.min(indemnizacion, maxImprocedente);
                    break;
                case 3: // Nulo
                    indemnizacion = 0;
                    break;
                default: // Disciplinario o sin indemnización
                    indemnizacion = 0;
                    break;
            }

            // 5. Finiquito (sin incluir indemnización)
            double totalFiniquito = salarioPorDiasTrabajados + importeVacaciones + pagasExtra;

            // 6. Total general (finiquito + indemnización)
            double totalLiquidacion = totalFiniquito + indemnizacion;

            // 7. Enviar resultados
            Intent intent = new Intent(this, ActivityResultadoFiniquito.class);
            intent.putExtra("salario", salarioPorDiasTrabajados);
            intent.putExtra("vacaciones", importeVacaciones);
            intent.putExtra("pagasExtra", pagasExtra);
            intent.putExtra("finiquito", totalFiniquito);
            intent.putExtra("indemnizacion", indemnizacion);
            intent.putExtra("total", totalLiquidacion);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error en el formato de los datos introducidos.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }





}

