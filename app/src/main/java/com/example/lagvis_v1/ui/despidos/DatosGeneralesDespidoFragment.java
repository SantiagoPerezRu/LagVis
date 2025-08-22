package com.example.lagvis_v1.ui.despidos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.databinding.FragmentDatosGeneralesDespidoBinding;

import java.util.Calendar;
import java.util.Locale;

public class DatosGeneralesDespidoFragment extends Fragment {

    public static final String EXTRA_SALARIO_DIARIO   = "com.example.lagvis_v1.EXTRA_SALARIO_DIARIO";
    public static final String EXTRA_MESES_TRABAJADOS = "com.example.lagvis_v1.EXTRA_MESES_TRABAJADOS";
    public static final String EXTRA_DIAS_TRABAJADOS  = "com.example.lagvis_v1.EXTRA_DIAS_TRABAJADOS";

    private FragmentDatosGeneralesDespidoBinding binding;
    private DatosGeneralesDespidoViewModel vm;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDatosGeneralesDespidoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this, new DatosGeneralesDespidoViewModelFactory())
                .get(DatosGeneralesDespidoViewModel.class);

        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                binding.btnCalcular.setEnabled(false);
            } else if (state instanceof UiState.Error) {
                binding.btnCalcular.setEnabled(true);
                String msg = ((UiState.Error<?>) state).message;
                Toast.makeText(requireContext(), msg != null ? msg : "Error", Toast.LENGTH_LONG).show();
            } else if (state instanceof UiState.Success) {
                binding.btnCalcular.setEnabled(true);
                DatosGeneralesDespidoViewModel.Resultado r =
                        ((UiState.Success<DatosGeneralesDespidoViewModel.Resultado>) state).data;

                Intent intent = new Intent(requireContext(), ActivityResultadoDespido.class);
                intent.putExtra(EXTRA_SALARIO_DIARIO, r.salarioDiario);
                intent.putExtra(EXTRA_MESES_TRABAJADOS, r.mesesTrabajados);
                intent.putExtra(EXTRA_DIAS_TRABAJADOS, r.diasTrabajados);
                intent.putExtra("fechaInicioFormatted", r.fechaInicioStr);
                intent.putExtra("fechaFinFormatted", r.fechaFinStr);
                startActivity(intent);
            }
        });

        setupDatePicker(binding.etFechaInicio);
        setupDatePicker(binding.etFechaFin);

        binding.btnCalcular.setOnClickListener(v ->
                vm.calcular(
                        binding.etFechaInicio.getText().toString(),
                        binding.etFechaFin.getText().toString(),
                        binding.etSalarioDiario.getText().toString()
                )
        );
    }

    private void setupDatePicker(@NonNull final android.widget.EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(
                    requireContext(),
                    (DatePicker view, int y, int m, int d) -> {
                        String formatted = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
                        editText.setText(formatted);
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            );
            dlg.show();
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
