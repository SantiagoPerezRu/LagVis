package FiniquitosPackage; // Cambia por tu paquete real

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R;

public class ActivityResultadoFiniquito extends AppCompatActivity {

    private TextView tvVacaciones, tvPagasExtras, tvFiniquito, tvIndemnizacion, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_finiquito); // Asegúrate de que coincida con el nombre XML

        // 1. Referencias a los TextView
        tvVacaciones = findViewById(R.id.tvVacaciones);
        tvPagasExtras = findViewById(R.id.tvPagasExtras);
        tvFiniquito = findViewById(R.id.tvFiniquito);
        tvIndemnizacion = findViewById(R.id.tvIndemnizacion);
        tvTotal = findViewById(R.id.tvTotal);

        // 2. Obtener los datos del Intent
        double vacaciones = getIntent().getDoubleExtra("vacaciones", 0.0);
        double pagasExtras = getIntent().getDoubleExtra("pagasExtras", 0.0);
        double finiquito = getIntent().getDoubleExtra("finiquito", 0.0);
        double indemnizacion = getIntent().getDoubleExtra("indemnizacion", 0.0);

        // 3. Mostrar cada valor con formato €
        tvVacaciones.setText(String.format("%.2f €", vacaciones));
        tvPagasExtras.setText(String.format("%.2f €", pagasExtras));
        tvFiniquito.setText(String.format("%.2f €", finiquito));
        tvIndemnizacion.setText(String.format("%.2f €", indemnizacion));

        // 4. Calcular total
        double total = finiquito + indemnizacion;
        tvTotal.setText(String.format("%.2f €", total));
    }
}
