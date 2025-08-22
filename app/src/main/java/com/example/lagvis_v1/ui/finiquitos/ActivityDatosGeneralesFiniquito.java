package com.example.lagvis_v1.ui.finiquitos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.databinding.ActivityDatosGeneralesFiniquitoBinding;

public class ActivityDatosGeneralesFiniquito extends AppCompatActivity {

    private ActivityDatosGeneralesFiniquitoBinding binding;
    private DatosGeneralesFiniquitoViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatosGeneralesFiniquitoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this, new DatosGeneralesFiniquitoViewModelFactory())
                .get(DatosGeneralesFiniquitoViewModel.class);

        // 🔴 FECHAS DESDE EL INTENT (vienen de CalculadoraFiniquitosFragment)
        String fechaInicio = getIntent().getStringExtra("fechaInicio");
        String fechaFin    = getIntent().getStringExtra("fechaFin");
        if (fechaInicio == null || fechaFin == null) {
            Toast.makeText(this, "Faltan fechas de inicio/fin", Toast.LENGTH_LONG).show();
            finish(); return;
        }
        vm.setFechasContrato(fechaInicio, fechaFin); // << CLAVE

        configurarSpinnerPagas();
        configurarSpinnerTipoDespido();

        vm.state.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.btnCalcularFiniquito.setEnabled(false);
            } else if (state instanceof UiState.Error) {
                binding.btnCalcularFiniquito.setEnabled(true);
                String msg = ((UiState.Error<?>) state).message;
                Toast.makeText(this, msg != null ? msg : "Error", Toast.LENGTH_LONG).show();
            } else if (state instanceof UiState.Success) {
                binding.btnCalcularFiniquito.setEnabled(true);
                DatosGeneralesFiniquitoViewModel.ResultadoFiniquito r =
                        ((UiState.Success<DatosGeneralesFiniquitoViewModel.ResultadoFiniquito>) state).data;

                Intent i = new Intent(this, ActivityResultadoFiniquito.class);
                i.putExtra("salario",       r.salarioPorDiasTrabajados);
                i.putExtra("vacaciones",    r.importeVacaciones);
                i.putExtra("pagasExtra",    r.pagasExtra);
                i.putExtra("finiquito",     r.totalFiniquito);
                i.putExtra("indemnizacion", r.indemnizacion);
                i.putExtra("total",         r.totalLiquidacion);
                // (opcional) reenvía fechas para mostrarlas
                i.putExtra("fechaInicio", fechaInicio);
                i.putExtra("fechaFin",    fechaFin);
                startActivity(i);
            }
        });

        binding.btnCalcularFiniquito.setOnClickListener(v ->
                vm.calcular(
                        binding.etSalarioAnual.getText().toString().trim(),
                        binding.etDiasVacaciones.getText().toString().trim(),
                        binding.spinnerPagas.getSelectedItemPosition(),       // 0=12, 1=14(no prorr), 2=prorr
                        binding.spinnerTipoDespido.getSelectedItemPosition()  // 0..3
                )
        );
    }

    private void configurarSpinnerPagas() {
        // Asegúrate de que el índice 1 sea "14 (no prorrateadas)"
        String[] opcionesPagas = {"12 pagas", "14 (no prorrateadas)", "Pagas prorrateadas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opcionesPagas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPagas.setAdapter(adapter);
    }

    private void configurarSpinnerTipoDespido() {
        String[] opcionesTipoDespido = {
                "Disciplinario (procedente)", // 0
                "Objetivo",                   // 1
                "Improcedente",               // 2
                "Nulo"                        // 3
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opcionesTipoDespido);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoDespido.setAdapter(adapter);
    }
}
