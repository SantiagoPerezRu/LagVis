// com/example/lagvis_v1/ui/auth/RecoverPassword.java
package com.example.lagvis_v1.ui.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseActivity;
import com.example.lagvis_v1.databinding.ActivityRecoverPasswordBinding;

public class RecoverPassword extends BaseActivity {

    private ActivityRecoverPasswordBinding binding;
    private AuthViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRecoverPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vm = new ViewModelProvider(this, new AuthViewModelFactory())
                .get(AuthViewModel.class);

        // Observa el estado de reset
        vm.reset.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.btnEnviar.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.btnEnviar.setEnabled(true);
                Drawable info = getDrawable(R.drawable.ic_info_outline);
                showCustomToast("¡Email enviado! Revisa tu correo.", info);
                binding.textoYaEnviado1.setVisibility(android.view.View.VISIBLE);
                binding.textoYaEnviado2.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof UiState.Error) {
                binding.btnEnviar.setEnabled(true);
                Drawable err = getDrawable(R.drawable.ic_error_outline);
                String msg = ((UiState.Error<?>) state).message;
                showCustomToast(msg != null ? msg : "No se pudo enviar el email", err);
            }
        });

        // Botones de navegación
        binding.textoYaEnviado2.setOnClickListener(v ->
                startActivity(new Intent(RecoverPassword.this, LoginActivity.class)));
        binding.textoRegistro.setOnClickListener(v ->
                startActivity(new Intent(RecoverPassword.this, RegisterActivity.class)));

        // Enviar reset
        binding.btnEnviar.setOnClickListener(v -> {
            String email = binding.emailEdittext.getText().toString().trim();
            if (email.isEmpty()) {
                Drawable err = getDrawable(R.drawable.ic_error_outline);
                showCustomToast("¡Por favor introduce un correo electrónico!", err);
            } else {
                vm.resetPassword(email);
            }
        });
    }
}
