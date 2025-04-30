package com.example.lagvis_v1;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdvancedFormRegister extends AppCompatActivity {

    AutoCompleteTextView autoCompleteComunidadesAutonomas;
    AutoCompleteTextView _autoCompleteTextViewSectores;
    ArrayAdapter<String> arrAdapterComunidadesAuto;
    ArrayAdapter<String> arrAdapterSectores;

    EditText _nombreEditText, _apellidoEdiText, _apellidoEdiText2;

    Button btnSiguiente;
    Button btnFecha;
    String fechaNacimiento;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_form_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnFecha = findViewById(R.id.btnFecha);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int ano = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AdvancedFormRegister.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        // Crear Calendar con fecha seleccionada
                        Calendar fechaSeleccionada = Calendar.getInstance();
                        fechaSeleccionada.set(i, i1, i2);
                        Calendar fechaActual = Calendar.getInstance();

                        if (fechaSeleccionada.after(fechaActual)) {
                            Toast.makeText(AdvancedFormRegister.this, "La fecha no puede ser futura", Toast.LENGTH_SHORT).show();
                        } else {
                            btnFecha.setText(i2 + "/" + (i1 + 1) + "/" + i);
                            fechaNacimiento = i2 + "/" + (i1 + 1) + "/" + i;
                            Toast.makeText(AdvancedFormRegister.this, "fecha" + fechaNacimiento, Toast.LENGTH_SHORT).show();

                        }
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        auth = FirebaseAuth.getInstance();

        String[] comunidades = getResources().getStringArray(R.array.comunidades);
        String[] sectores = getResources().getStringArray(R.array.sectores);

        btnSiguiente = findViewById(R.id.btnEnviar);

        autoCompleteComunidadesAutonomas = findViewById(R.id.autoCompleteTextViewComunidadAutonoma);
        _autoCompleteTextViewSectores = findViewById(R.id.autoCompleteTextViewSectores);

        arrAdapterComunidadesAuto = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, comunidades);
        arrAdapterSectores = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sectores);

        autoCompleteComunidadesAutonomas.setAdapter(arrAdapterComunidadesAuto);
        _autoCompleteTextViewSectores.setAdapter(arrAdapterSectores);

        autoCompleteComunidadesAutonomas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        _autoCompleteTextViewSectores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        _nombreEditText = findViewById(R.id.nombreEditText);
        _apellidoEdiText = findViewById(R.id.apellidoEditText);
        _apellidoEdiText2 = findViewById(R.id.apellido2EditText);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarNuevoUsuario();
            }
        });
    }

    private void registrarNuevoUsuario() {
        ProgressDialog progressDialog = new ProgressDialog(AdvancedFormRegister.this);
        progressDialog.setMessage("Cargando...");

        // Validación de la fecha de nacimiento
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar una fecha de nacimiento", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            String[] partes = fechaNacimiento.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]) - 1;
            int ano = Integer.parseInt(partes[2]);

            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(ano, mes, dia);

            Calendar fechaActual = Calendar.getInstance();

            if (fechaSeleccionada.after(fechaActual)) {
                Toast.makeText(this, "La fecha de nacimiento no puede ser futura", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar la fecha de nacimiento", Toast.LENGTH_LONG).show();
            return;
        }

        if (_nombreEditText.getText().toString().trim().isEmpty() ||
                _apellidoEdiText.getText().toString().trim().isEmpty() ||
                _apellidoEdiText2.getText().toString().trim().isEmpty() ||
                autoCompleteComunidadesAutonomas.getText().toString().trim().isEmpty() ||
                _autoCompleteTextViewSectores.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Debe introducir todos los campos!", Toast.LENGTH_LONG).show();

        } else {

            String name = _nombreEditText.getText().toString().trim();
            String surName1 = _apellidoEdiText.getText().toString().trim();
            String surName2 = _apellidoEdiText2.getText().toString().trim();
            String comunidadAutonoma = autoCompleteComunidadesAutonomas.getText().toString().trim();
            String sectorLaboral = _autoCompleteTextViewSectores.getText().toString().trim();
            String fecha2Nacimiento = btnFecha.getText().toString();
            String idComunidadAutonoma = String.valueOf(obtenerIdComunidad(comunidadAutonoma));
            String idSector = String.valueOf(obtenerIdSector(sectorLaboral));
            FirebaseUser usuarioFireBase = auth.getCurrentUser();
            String uidFireBase = usuarioFireBase.getUid();

            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.50/lagVis/insertar_.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equalsIgnoreCase("Datos insertados correctamente")) {
                        progressDialog.dismiss();
                        startActivity(new Intent(AdvancedFormRegister.this, LoginActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AdvancedFormRegister.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", uidFireBase);
                    params.put("nombre", name);
                    params.put("apellido", surName1);
                    params.put("apellido2", surName2);
                    params.put("comunidad_id", idComunidadAutonoma);
                    params.put("sector_id", idSector);
                    params.put("fechaNacimiento", fecha2Nacimiento);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(AdvancedFormRegister.this);
            requestQueue.add(request);
        }
    }

    public int obtenerIdComunidad(String comunidadAutonoma) {
        switch (comunidadAutonoma) {
            case "Andalucía": return 1;
            case "Aragón": return 2;
            case "Asturias": return 3;
            case "Baleares": return 4;
            case "Canarias": return 5;
            case "Cantabria": return 6;
            case "Castilla-La Mancha": return 7;
            case "Castilla y León": return 8;
            case "Cataluña": return 9;
            case "Extremadura": return 10;
            case "Galicia": return 11;
            case "Comunidad de Madrid": return 12;
            case "Murcia": return 13;
            case "Navarra": return 14;
            case "La Rioja": return 15;
            case "País Vasco": return 16;
            case "Valencia": return 17;
            default: return -1;
        }
    }

    public int obtenerIdSector(String sector) {
        switch (sector) {
            case "Hosteleria": return 1;
            case "Construcción": return 2;
            case "Informatica": return 3;
            default: return -1;
        }
    }
}
