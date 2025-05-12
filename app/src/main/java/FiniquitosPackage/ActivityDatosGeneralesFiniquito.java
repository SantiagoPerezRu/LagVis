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
        // Opciones para el tipo de despido
        String[] opcionesTipoDespido = {"Procedente", "Improcedente", "Nulo"}; //Añade más tipos si es necesario
        // Crear un ArrayAdapter usando un layout por defecto del sistema
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesTipoDespido);
        // Especificar el layout a usar cuando se despliega la lista de opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asignar el adapter al spinner
        spinnerTipoDespido.setAdapter(adapter);
    }

    private void calcularFiniquito() {
        // Obtener los valores introducidos por el usuario
        String salarioAnualStr = etSalarioAnual.getText().toString().trim();
        String diasVacacionesStr = etDiasVacaciones.getText().toString().trim();
        int posicionPagas = spinnerPagas.getSelectedItemPosition();
        int posicionTipoDespido = spinnerTipoDespido.getSelectedItemPosition();

        // Validar que los campos obligatorios estén completos
        if (salarioAnualStr.isEmpty() || diasVacacionesStr.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el salario anual y los días de vacaciones.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir los valores a los tipos de datos adecuados
        double salarioAnual = Double.parseDouble(salarioAnualStr);
        int diasVacaciones = Integer.parseInt(diasVacacionesStr);

        // Realizar el cálculo del finiquito (aquí va tu lógica de cálculo)
        // ...
        double importeFiniquito = calcularImporteFiniquito(salarioAnual, diasTrabajados, diasVacaciones, posicionPagas, posicionTipoDespido);
        Toast.makeText(this, "aa"+importeFiniquito, Toast.LENGTH_SHORT).show();
        // Crear un Intent para mostrar el resultado
        Intent intent = new Intent(this, ActivityResultadoFiniquito.class); // Reemplaza ResultadoFiniquitoActivity.class
        intent.putExtra("importeFiniquito", importeFiniquito);
        intent.putExtra("diasTrabajados", diasTrabajados); //Si los necesitas pasar
        startActivity(intent);
        // Mostrar un mensaje de error si los datos no son válidos
         //catch (NumberFormatException e) {
           // Toast.makeText(this, "Error en el formato de los datos introducidos.", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
      //  }

    }
    //Método para calcular el finiquito.
    private double calcularImporteFiniquito(double salarioAnual, long diasTrabajados, int diasVacaciones, int posicionPagas, int posicionTipoDespido) {
        double importe = 0;

        double salarioDiario = salarioAnual / 365;

        double calculoSalarioDiarioDiasTrabajados = salarioDiario * diasTrabajados;

        double calculoValorVacacionesNoDisfrutadas = diasVacaciones * salarioDiario;

        double importeTotal = calculoSalarioDiarioDiasTrabajados + calculoValorVacacionesNoDisfrutadas;

        //Toast.makeText(this, ""+importeTotal, Toast.LENGTH_SHORT).show();

        return importeTotal;
    }
}

