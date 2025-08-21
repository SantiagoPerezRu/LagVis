package com.example.lagvis_v1.ui.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseActivity;
import com.example.lagvis_v1.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel vm;

    private Drawable errorIcon;
    private Drawable checkIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        errorIcon = getDrawable(R.drawable.ic_error_outline);
        checkIcon = getDrawable(R.drawable.ic_check_circle);

        vm = new ViewModelProvider(this, new AuthViewModelFactory())
                .get(AuthViewModel.class);

        // Observa el estado de registro
        vm.signup.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.registerButton.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.registerButton.setEnabled(true);
                // Éxito: cuenta creada + email de verificación enviado
                showCustomToast("Registro correcto. ¡Verifica tu email!", checkIcon);
                // Navega a formulario avanzado
                startActivity(new Intent(RegisterActivity.this, AdvancedFormRegister.class));
                finish();
            } else if (state instanceof UiState.Error) {
                binding.registerButton.setEnabled(true);
                String msg = ((UiState.Error<?>) state).message;
                showCustomToast(msg != null ? msg : "ERROR ¡Por favor revisa todos los campos!", errorIcon);
            }
        });

        binding.registerButton.setOnClickListener(v -> registerNewUser());
    }

    private void registerNewUser() {
        String email = binding.emailEdittext.getText().toString().trim();
        String password = binding.passwordEdittext.getText().toString().trim();
        String password2 = binding.passwordEdittext2.getText().toString().trim();

        if (!password.equals(password2)) {
            showCustomToast("Por favor, introduce la misma contraseña!", errorIcon);
            return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            showCustomToast("Por favor, introduce unas credenciales!", errorIcon);
            return;
        }
        // mínimo 6 chars, una mayúscula, una minúscula y un número
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$";
        if (!password.matches(passwordPattern)) {
            showCustomToast("La contraseña debe tener al menos 6 caracteres, incluir una mayúscula, una minúscula y un número!", errorIcon);
            return;
        }

        // MVVM: delega en el ViewModel
        vm.signUp(email, password);
    }
}
