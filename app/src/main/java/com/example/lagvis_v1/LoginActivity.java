package com.example.lagvis_v1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private EditText emailTextView, passwordTextView;
    private Button button;
    private FirebaseAuth auth;
    TextView _hola;
    private TextView txtRegistro, txtRecuperarContraseña1, txtRecuperarContraseña2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.email_edittext);
        passwordTextView = findViewById(R.id.password_edittext);
        button = findViewById(R.id.login_button);

        button.setOnClickListener(v -> loginUserAccount());

        txtRegistro = findViewById(R.id.textoRegistro);

        txtRecuperarContraseña1 = findViewById(R.id.textoPerdidaContra1);
        txtRecuperarContraseña2 = findViewById(R.id.textoPerdidaContra2);


        txtRecuperarContraseña2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecoverPassword.class));
            }
        });


        txtRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
    }

    private void loginUserAccount() {
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_LONG).show();
            return;
        }

        // Login existing user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso
                            Drawable checkIcon = getDrawable(R.drawable.ic_check_circle);
                            showCustomToast("¡Inicio de sesión exitoso!", checkIcon);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Inicio de sesión fallido
                            Drawable errorIcon = getDrawable(R.drawable.ic_error_outline);
                            showCustomToast("¡Inicio de sesión fallido!", errorIcon);
                            txtRecuperarContraseña1.setVisibility(View.VISIBLE);
                            txtRecuperarContraseña2.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


}