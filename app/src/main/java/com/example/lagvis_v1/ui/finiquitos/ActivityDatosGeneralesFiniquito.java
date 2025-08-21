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


import FiniquitosPackage.ActivityResultadoFiniquito;

public class ActivityDatosGeneralesFiniquito extends AppCompatActivity {

    private ActivityDatosGeneralesFiniquitoBinding binding;
    private DatosGeneralesFiniquitoViewModel vm;
    private long diasTrabajados;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatosGeneralesFiniquitoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // VM
        vm = new ViewModelProvider(this, new DatosGeneralesFiniquitoViewModelFactory())
                .get(DatosGeneralesFiniquitoViewModel.class);

        // Días trabajados del Intent
        diasTrabajados = getIntent().getLongExtra("diasTrabajados", 0L);
        vm.setDiasTrabajados(diasTrabajados);

        // Spinners
        configurarSpinnerPagas();
        configurarSpinnerTipoDespido();

        // Observers
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
                i.putExtra("salario", r.salarioPorDiasTrabajados);
                i.putExtra("vacaciones", r.importeVacaciones);
                i.putExtra("pagasExtra", r.pagasExtra);
                i.putExtra("finiquito", r.totalFiniquito);
                i.putExtra("indemnizacion", r.indemnizacion);
                i.putExtra("total", r.totalLiquidacion);
                startActivity(i);
            }
        });

        // Click calcular
        binding.btnCalcularFiniquito.setOnClickListener(v ->
                vm.calcular(
                        binding.etSalarioAnual.getText().toString().trim(),
                        binding.etDiasVacaciones.getText().toString().trim(),
                        binding.spinnerPagas.getSelectedItemPosition(),
                        binding.spinnerTipoDespido.getSelectedItemPosition()
                )
        );
    }

    private void configurarSpinnerPagas() {
        String[] opcionesPagas = {"12 pagas", "14 pagas", "Pagas prorrateadas"};
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
