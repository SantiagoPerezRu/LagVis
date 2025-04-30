package com.example.lagvis_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RecoverPassword extends AppCompatActivity {

    private TextView recuperarPasswdEmail, textoYaEnviado1, textoYaEnviado2, textoRegistrateAqui;
    private Button btnEnviar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recover_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        recuperarPasswdEmail = findViewById(R.id.email_edittext);
        textoYaEnviado1 = findViewById(R.id.textoYaEnviado1);
        textoYaEnviado2 = findViewById(R.id.textoYaEnviado2);
        textoRegistrateAqui = findViewById(R.id.textoRegistro);

        textoYaEnviado2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RecoverPassword.this, LoginActivity.class));

            }
        });

        textoRegistrateAqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecoverPassword.this, RegisterActivity.class));
            }
        });

        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(recuperarPasswdEmail.getText().toString().isEmpty()){
                    Toast.makeText(RecoverPassword.this, "¡Por favor introduce un correo electronico!", Toast.LENGTH_LONG).show();
                }else {
                    recuperarContrasena();
                    textoYaEnviado1.setVisibility(View.VISIBLE);
                    textoYaEnviado2.setVisibility(View.VISIBLE);
                }


            }
        });



    }

    private void recuperarContrasena(){

        String email = recuperarPasswdEmail.getText().toString().trim();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecoverPassword.this, "Email enviado SOLO DEPURACION!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}