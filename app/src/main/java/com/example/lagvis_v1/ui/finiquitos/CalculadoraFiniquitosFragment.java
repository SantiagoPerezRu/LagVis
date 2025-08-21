package com.example.lagvis_v1.ui.finiquitos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseFragment;
import com.example.lagvis_v1.databinding.FragmentThirdBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalculadoraFiniquitosFragment extends BaseFragment {

    private FragmentThirdBinding binding;
    private CalculadoraFiniquitosViewModel vm;

    private Calendar calContrato, calDespido;
    private SimpleDateFormat dateFormatter;
    private Drawable errorIcon;

    public CalculadoraFiniquitosFragment() {}

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline);
        calContrato = Calendar.getInstance();
        calDespido  = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        vm = new ViewModelProvider(this, new CalculadoraFiniquitosViewModelFactory())
                .get(CalculadoraFiniquitosViewModel.class);

        vm.next.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                binding.btnSiguiente.setEnabled(false);
            } else if (state instanceof UiState.Error) {
                binding.btnSiguiente.setEnabled(true);
                String msg = ((UiState.Error<?>) state).message;
                mostrarToastPersonalizado(msg != null ? msg : "Error", errorIcon);
            } else if (state instanceof UiState.Success) {
                binding.btnSiguiente.setEnabled(true);
                long diasTrabajados = ((UiState.Success<Long>) state).data;
                Intent intent = new Intent(requireContext(), ActivityDatosGeneralesFiniquito.class);
                intent.putExtra("diasTrabajados", diasTrabajados);
                startActivity(intent);
            }
        });

        binding.etFechaContrato.setOnClickListener(v -> showDatePicker(calContrato, true));
        binding.etFechaDespido.setOnClickListener(v -> showDatePicker(calDespido, false));
        binding.btnSiguiente.setOnClickListener(v ->
                vm.onNextClicked(
                        binding.etFechaContrato.getText().toString(),
                        binding.etFechaDespido.getText().toString()
                )
        );
    }

    private void showDatePicker(Calendar cal, boolean isContrato) {
        new DatePickerDialog(
                requireContext(),
                (picker, y, m, d) -> {
                    cal.set(y, m, d);
                    String f = dateFormatter.format(cal.getTime());
                    if (isContrato) binding.etFechaContrato.setText(f);
                    else binding.etFechaDespido.setText(f);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
