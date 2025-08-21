package com.example.lagvis_v1.ui.convenio;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseFragment;
import com.example.lagvis_v1.databinding.FragmentFirstBinding;

public class ConvenioSelectorFrag extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1, mParam2;

    private FragmentFirstBinding binding;
    private ConvenioSelectorViewModel vm;

    public static ConvenioSelectorFrag newInstance(String p1, String p2) {
        ConvenioSelectorFrag fragment = new ConvenioSelectorFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, p1);
        args.putString(ARG_PARAM2, p2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this, new ConvenioSelectorViewModelFactory()).get(ConvenioSelectorViewModel.class);

        // Adapters
        String[] comunidades = getResources().getStringArray(R.array.comunidades_autonomas);
        String[] sectores    = getResources().getStringArray(R.array.sectores);
        binding.autoCompleteTextViewComunidades.setAdapter(
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, comunidades)
        );
        binding.autoCompleteTextViewSectores.setAdapter(
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, sectores)
        );

        // Observa navegación/errores
        vm.nav.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Error) {
                binding.textViewError.setText(((UiState.Error<?>) state).message);
                binding.textViewError.setVisibility(View.VISIBLE);
            } else if (state instanceof UiState.Success) {
                binding.textViewError.setVisibility(View.GONE);
                ConvenioSelectorViewModel.NavData d = ((UiState.Success<ConvenioSelectorViewModel.NavData>) state).data;

                // Verifica que exista el recurso antes de navegar
                int resId = getResources().getIdentifier(
                        d.archivo.replace(".xml",""), "raw", requireContext().getPackageName()
                );
                if (resId == 0) {
                    binding.textViewError.setText("No se encontró un convenio para esta combinación.");
                    binding.textViewError.setVisibility(View.VISIBLE);
                    return;
                }

                Intent i = new Intent(requireActivity(), ConvenioVisualizer.class);
                i.putExtra("archivo_convenio", d.archivo);
                i.putExtra("comunidadId", d.comunidadId);
                i.putExtra("sectorId", d.sectorId);
                startActivity(i);
            }
        });

        binding.btnEnviar.setOnClickListener(v -> {
            String comunidad = binding.autoCompleteTextViewComunidades.getText().toString();
            String sector    = binding.autoCompleteTextViewSectores.getText().toString();
            vm.onSiguiente(comunidad, sector);
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
