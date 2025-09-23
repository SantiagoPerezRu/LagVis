package com.example.lagvis_v1.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseActivity;
import com.example.lagvis_v1.databinding.ActivityLoginBinding;
import com.example.lagvis_v1.ui.compose.HomeMenuActivityKt;
import com.example.lagvis_v1.ui.convenio.ConvenioSelectorFrag;
import com.example.lagvis_v1.ui.main.MainActivity;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel vm;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel (MVVM)
        vm = new ViewModelProvider(this, new AuthViewModelFactory())
                .get(AuthViewModel.class);

        // Remember me prefs
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            binding.emailEdittext.setText(sharedPreferences.getString("email", ""));
            binding.passwordEdittext.setText(sharedPreferences.getString("password", ""));
            binding.checkboxRemember.setChecked(true);
        }

        // Observers
        vm.login.observe(this, state -> {
            if (state instanceof UiState.Loading) {
                binding.loginButton.setEnabled(false);
            } else if (state instanceof UiState.Success) {
                binding.loginButton.setEnabled(true);

                // Guardar credenciales si procede
                if (binding.checkboxRemember.isChecked()) {
                    editor.putBoolean("remember", true);
                    editor.putString("email", binding.emailEdittext.getText().toString().trim());
                    editor.putString("password", binding.passwordEdittext.getText().toString().trim());
                } else {
                    editor.clear();
                }
                editor.apply();

                Drawable checkIcon = getDrawable(R.drawable.ic_check_circle);
                showCustomToast("¡Inicio de sesión exitoso!", checkIcon);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else if (state instanceof UiState.Error) {
                binding.loginButton.setEnabled(true);
                Drawable errorIcon = getDrawable(R.drawable.ic_error_outline);
                showCustomToast("¡Inicio de sesión fallido!", errorIcon);
                binding.textoPerdidaContra1.setVisibility(android.view.View.VISIBLE);
                binding.textoPerdidaContra2.setVisibility(android.view.View.VISIBLE);
            }
        });

        // Listeners
        binding.loginButton.setOnClickListener(v -> attemptLogin());
        binding.textoPerdidaContra2.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RecoverPassword.class)));
        binding.textoRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = binding.emailEdittext.getText().toString().trim();
        String password = binding.passwordEdittext.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Drawable errorIcon = getDrawable(R.drawable.ic_error_outline);
            showCustomToast("¡Por favor introduzca credenciales!", errorIcon);
            return;
        }
        vm.signIn(email, password); // 👉 MVVM: delega en el ViewModel
    }
}
