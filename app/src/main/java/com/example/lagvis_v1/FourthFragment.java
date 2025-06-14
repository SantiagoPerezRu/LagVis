package com.example.lagvis_v1;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FourthFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView nombreTextView, apellidoTextView, apellido2TextView, sectorLaboralTextView, comunidadAutonomaTextView, fechaNacimientoTextView;

    private static final String URL_MOSTRAR_DATOS = LagVisConstantes.ENDPOINT_MOSTRAR;

    private String uidUsuario;

    private Button recuperarPasswd;
    private FirebaseAuth auth;

    public FourthFragment() {
        // Required empty public constructor
    }

    public static FourthFragment newInstance(String param1, String param2) {
        FourthFragment fragment = new FourthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_four, container, false);

        nombreTextView = view.findViewById(R.id.textViewNombre);
        apellidoTextView = view.findViewById(R.id.textViewApellido1);
        apellido2TextView = view.findViewById(R.id.textViewApellido2);
        sectorLaboralTextView = view.findViewById(R.id.textViewSectorLaboral);
        comunidadAutonomaTextView = view.findViewById(R.id.textViewComunidadAutonoma);
        fechaNacimientoTextView = view.findViewById(R.id.textViewFechaNacimiento);
        recuperarPasswd = view.findViewById(R.id.recuperarPasswd);

        recuperarPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = auth.getCurrentUser();
                uidUsuario = currentUser.getUid();

                String emailUsuario = currentUser.getEmail();

                auth.sendPasswordResetEmail(emailUsuario);


            }});


        obtenerDatosUsuario(view);
        return view;
    }

    private void obtenerDatosUsuario(View view) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            uidUsuario = currentUser.getUid();
            Log.d("UID Usuario", "UID: " + uidUsuario);
        } else {
            Toast.makeText(getContext(), "No hay usuario logueado", Toast.LENGTH_LONG).show();
            Log.e("UID Usuario", "No hay usuario logueado");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MOSTRAR_DATOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPUESTA_CRUDA", "Respuesta completa: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("exito").equals("1")) {

                                if (jsonObject.has("datos")) {
                                    JSONArray datosArray = jsonObject.getJSONArray("datos");

                                    if (datosArray.length() > 0) {
                                        JSONObject datosUsuario = datosArray.getJSONObject(0);

                                        String nombre = datosUsuario.optString("nombre", "");
                                        String apellido = datosUsuario.optString("apellido", "");
                                        String apellido2 = datosUsuario.optString("apellido2", "");
                                        String sectorLaboral = datosUsuario.optString("sector_laboral", "");
                                        String comunidadAutonoma = datosUsuario.optString("comunidad_autonoma", "");
                                        String fechaNacimiento = datosUsuario.optString("fecha_nacimiento", "");

                                        nombreTextView.setText(nombre);
                                        apellidoTextView.setText(apellido);
                                        apellido2TextView.setText(apellido2);
                                        sectorLaboralTextView.setText(sectorLaboral);
                                        comunidadAutonomaTextView.setText(comunidadAutonoma);
                                        fechaNacimientoTextView.setText(fechaNacimiento);
                                    } else {
                                        mostrarToastPersonalizado("No se encontraron datos para el usuario.", R.drawable.ic_error_outline);
                                    }
                                } else {
                                    mostrarToastPersonalizado("La respuesta no contiene datos del usuario.", R.drawable.ic_error_outline);
                                }

                            } else {
                                String mensaje = jsonObject.has("mensaje") ? jsonObject.getString("mensaje") : "Ocurrió un error inesperado.";
                                mostrarToastPersonalizado(mensaje, R.drawable.ic_error_outline);
                                Log.e("Error JSON", "Servidor respondió con error: " + mensaje);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", "Error al parsear JSON: " + e.getMessage());
                            mostrarToastPersonalizado("Error al procesar la respuesta del servidor.", R.drawable.ic_error_outline);
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error en la petición: " + error.getMessage());
                        Toast.makeText(getContext(), "Error de conexión Volley: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uidUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    /* Codigo repetido recordar borrar!!!!!!!!!!!!!!!!!!!!!!1*/
    public void mostrarToastPersonalizado(String message, int iconResId) {
        if (getContext() == null) return; 
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getView().findViewById(R.id.custom_toast_container)); 
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        ImageView toastIcon = (ImageView) layout.findViewById(R.id.toast_icon);

        if (iconResId != 0) {
            toastIcon.setImageResource(iconResId);
            toastIcon.setVisibility(View.VISIBLE);
        } else {
            toastIcon.setVisibility(View.GONE);
        }
        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


}

