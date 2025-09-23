// com/example/lagvis_v1/ui/FourthFragment.java
package com.example.lagvis_v1.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseFragment;
import com.example.lagvis_v1.databinding.FragmentFourBinding;
import com.example.lagvis_v1.dominio.model.UserProfile;
import com.example.lagvis_v1.ui.auth.AuthViewModel;
import com.example.lagvis_v1.ui.auth.AuthViewModelFactory;

public class ProfileViewFrag extends BaseFragment { // extiende tu BaseFragment para usar toasts comunes

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentFourBinding binding;

    private ProfileViewModel vm;
    private AuthViewModel authVm;

    public ProfileViewFrag() {}

    public static ProfileViewFrag newInstance(String p1, String p2) {
        ProfileViewFrag fragment = new ProfileViewFrag();
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

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFourBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // VMs
        vm = new ViewModelProvider(this, new ProfileViewModelFactory()).get(ProfileViewModel.class);
        authVm = new ViewModelProvider(this, new AuthViewModelFactory()).get(AuthViewModel.class);

        // Observa estado de perfil
        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                // podrías mostrar un shimmer / deshabilitar botones
            } else if (state instanceof UiState.Success) {
                UserProfile p = ((UiState.Success<UserProfile>) state).data;
                renderProfile(p);
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).message;
                mostrarToastPersonalizado(msg != null ? msg : "Error al cargar el perfil", getResources().getDrawable(R.drawable.ic_error_outline));
                clearProfile();
            }
        });

        // Botón “recuperar contraseña”
        binding.recuperarPasswd.setOnClickListener(v -> {
            String email = authVm.currentEmailOrNull();
            if (email == null || email.isEmpty()) {
                mostrarToastPersonalizado("No hay email de usuario", getResources().getDrawable(R.drawable.ic_error_outline));
            } else {
                authVm.resetPassword(email); // usa el VM de auth (ya implementado)
                mostrarToastPersonalizado("Email de recuperación enviado (si existe).", getResources().getDrawable(R.drawable.ic_info_outline));
            }
        });

        // Cargar perfil con el UID actual
        String uid = authVm.uidOrNull();
        if (uid == null) {
            mostrarToastPersonalizado("No hay usuario logueado", getResources().getDrawable(R.drawable.ic_error_outline));
        } else {
            vm.load(uid);
        }
    }

    private void renderProfile(UserProfile p){
        binding.textViewNombre.setText(p.nombre);
        binding.textViewApellido1.setText(p.apellido);
        binding.textViewApellido2.setText(p.apellido2);
        binding.textViewSectorLaboral.setText(p.sectorLaboral);
        binding.textViewComunidadAutonoma.setText(p.comunidadAutonoma);
        binding.textViewFechaNacimiento.setText(p.fechaNacimiento);
    }

    private void clearProfile(){
        binding.textViewNombre.setText("");
        binding.textViewApellido1.setText("");
        binding.textViewApellido2.setText("");
        binding.textViewSectorLaboral.setText("");
        binding.textViewComunidadAutonoma.setText("");
        binding.textViewFechaNacimiento.setText("");
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
