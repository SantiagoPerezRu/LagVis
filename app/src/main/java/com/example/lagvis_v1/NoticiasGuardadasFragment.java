package com.example.lagvis_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import api.NewsItem;

public class NoticiasGuardadasFragment extends BaseFragment {

    private TextView tvSavedTituloNoticia;
    private TextView tvSavedFechaNoticiaLabel;
    private TextView tvSavedFechaNoticia;
    private TextView tvSavedCreadorNoticiaLabel;
    private TextView tvSavedCreadorNoticia;
    private TextView tvSavedEnlaceNoticia;
    private Button btnSavedAnterior;
    private Button btnSavedSiguiente;

    private List<NewsItem> listaNoticiasGuardadas;
    private int indiceNoticiaActual = 0;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    public NoticiasGuardadasFragment() {
        // Constructor público vacío requerido
    }

    public static NoticiasGuardadasFragment newInstance() {
        return new NoticiasGuardadasFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_noticias_guardadas, container, false);

        // Inicializa las vistas
        tvSavedTituloNoticia = view.findViewById(R.id.tvSavedTituloNoticia);
        tvSavedFechaNoticiaLabel = view.findViewById(R.id.tvSavedFechaNoticiaLabel);
        tvSavedFechaNoticia = view.findViewById(R.id.tvSavedFechaNoticia);
        tvSavedCreadorNoticiaLabel = view.findViewById(R.id.tvSavedCreadorNoticiaLabel);
        tvSavedCreadorNoticia = view.findViewById(R.id.tvSavedCreadorNoticia);
        tvSavedEnlaceNoticia = view.findViewById(R.id.tvSavedEnlaceNoticia);
        btnSavedAnterior = view.findViewById(R.id.btnSavedAnterior);
        btnSavedSiguiente = view.findViewById(R.id.btnSavedSiguiente);

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Configura el ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando noticias guardadas...");
        progressDialog.setCancelable(false);

        // Deshabilita los botones al inicio
        btnSavedAnterior.setEnabled(false);
        btnSavedSiguiente.setEnabled(false);

        // Configura los listeners para los botones de navegación
        btnSavedAnterior.setOnClickListener(v -> {
            if (listaNoticiasGuardadas != null && !listaNoticiasGuardadas.isEmpty()) {
                indiceNoticiaActual--;
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });

        btnSavedSiguiente.setOnClickListener(v -> {
            if (listaNoticiasGuardadas != null && !listaNoticiasGuardadas.isEmpty()) {
                indiceNoticiaActual++;
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });

        return view;
    }
// Una vez que la vista ha sido creada, intenta cargar las noticias
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarNoticiasGuardadas();
    }

    private void cargarNoticiasGuardadas() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Drawable errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
            mostrarToastPersonalizado("¡Debes iniciar sesión para ver tus noticias guardadas!", errorIcon);
            tvSavedTituloNoticia.setText("Inicia sesión para ver tus noticias guardadas.");
            ocultarDetallesNoticia();
            return;
        }

        String uidUsuario = currentUser.getUid();
        progressDialog.show(); // Muestra el diálogo de progreso
        
        String url = LagVisConstantes.ENDPOINT_LISTAR_NOTICIAS;

        //Llamamos al scrip PHP
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss(); // Oculta el diálogo de progreso
                        try {
                            // Parsear la respuesta completa como un JSONObject
                            JSONObject jsonResponse = new JSONObject(response);

                            // Verificar el estado de éxito, el script si hay noticias y salen bien devuelve un mensaje de éxito
                            String exito = jsonResponse.getString("exito");
                            String mensaje = jsonResponse.optString("mensaje", ""); 

                            if ("1".equals(exito)) {
                                // Si el 'exito' es 1, entonces obtenemos el JSONArray 'datos'
                                JSONArray jsonArray = jsonResponse.getJSONArray("datos");

                                listaNoticiasGuardadas = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newsJson = jsonArray.getJSONObject(i);
                                    String id = newsJson.optString("id"); 
                                    String title = newsJson.optString("titulo");
                                    String link = newsJson.optString("enlace");
                                    String pubDate = newsJson.optString("fecha");
                                    String creator = newsJson.optString("creador");

                                    //Guardar noticia en la lista
                                    listaNoticiasGuardadas.add(new NewsItem(title, link, pubDate, creator));
                                }

                                if (listaNoticiasGuardadas.isEmpty()) {
                                    tvSavedTituloNoticia.setText("No tienes noticias guardadas aún.");
                                    ocultarDetallesNoticia();
                                } else {
                                    indiceNoticiaActual = 0;
                                    mostrarNoticiaActual();
                                    actualizarEstadoBotones();
                                    mostrarDetallesNoticia();
                                }

                            } else {
                                // La operación no tuvo éxito (exito = "0")
                                // Mostrar mensaje de error del servidor
                                Log.e("SavedNewsFragment", "Operación fallida: " + mensaje);
                                tvSavedTituloNoticia.setText("Error al cargar noticias guardadas: " + mensaje);
                                ocultarDetallesNoticia();
                                Drawable errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
                                mostrarToastPersonalizado("Error: " + mensaje, errorIcon);
                            }
                            

                        } catch (JSONException e) {
                            Log.e("SavedNewsFragment", "Error al parsear JSON: " + e.getMessage());
                            tvSavedTituloNoticia.setText("Error al cargar noticias guardadas.");
                            ocultarDetallesNoticia();
                            Drawable errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
                            mostrarToastPersonalizado("Error al procesar los datos.", errorIcon);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("SavedNewsFragment", "Error de Volley: " + error.toString());
                        String errorMessage = "Error al conectar con el servidor.";
                        if (error.networkResponse != null) {
                            errorMessage += " Código: " + error.networkResponse.statusCode;
                        }
                        tvSavedTituloNoticia.setText(errorMessage);
                        ocultarDetallesNoticia();
                        Drawable errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
                        mostrarToastPersonalizado(errorMessage, errorIcon);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uidUsuario);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void mostrarNoticiaActual() {
        if (listaNoticiasGuardadas != null && !listaNoticiasGuardadas.isEmpty() &&
                indiceNoticiaActual >= 0 && indiceNoticiaActual < listaNoticiasGuardadas.size()) {

            NewsItem noticia = listaNoticiasGuardadas.get(indiceNoticiaActual);

            tvSavedTituloNoticia.setText(noticia.title);
            tvSavedFechaNoticia.setText(noticia.pubDate);
            tvSavedCreadorNoticia.setText(noticia.creator);

            // Configurar el ClickableSpan para el enlace
            String textoEnlace = "Pincha aquí para ir al enlace de la noticia!";
            SpannableString spannableString = new SpannableString(textoEnlace);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.link));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("SavedNewsFragment", "Error al abrir la URL: " + noticia.link, e);
                        Drawable errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
                        mostrarToastPersonalizado("No se pudo abrir el enlace.", errorIcon);
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.BLUE); 
                    ds.setUnderlineText(true); 
                }
            };
            
            spannableString.setSpan(clickableSpan, 0, 11, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvSavedEnlaceNoticia.setText(spannableString);
            tvSavedEnlaceNoticia.setMovementMethod(LinkMovementMethod.getInstance()); // Necesario para que el ClickableSpan funcione
        }
    }

    private void actualizarEstadoBotones() {
        if (listaNoticiasGuardadas == null || listaNoticiasGuardadas.isEmpty()) {
            btnSavedAnterior.setEnabled(false);
            btnSavedSiguiente.setEnabled(false);
        } else if (indiceNoticiaActual == 0) {
            btnSavedAnterior.setEnabled(false);
            btnSavedSiguiente.setEnabled(listaNoticiasGuardadas.size() > 1);
        } else if (indiceNoticiaActual == listaNoticiasGuardadas.size() - 1) {
            btnSavedSiguiente.setEnabled(false);
            btnSavedAnterior.setEnabled(true);
        } else {
            btnSavedAnterior.setEnabled(true);
            btnSavedSiguiente.setEnabled(true);
        }
    }

    private void ocultarDetallesNoticia() {
        tvSavedFechaNoticiaLabel.setVisibility(View.GONE);
        tvSavedFechaNoticia.setVisibility(View.GONE);
        tvSavedCreadorNoticiaLabel.setVisibility(View.GONE);
        tvSavedCreadorNoticia.setVisibility(View.GONE);
        tvSavedEnlaceNoticia.setVisibility(View.GONE);
        btnSavedAnterior.setEnabled(false);
        btnSavedSiguiente.setEnabled(false);
    }

    private void mostrarDetallesNoticia() {
        tvSavedFechaNoticiaLabel.setVisibility(View.VISIBLE);
        tvSavedFechaNoticia.setVisibility(View.VISIBLE);
        tvSavedCreadorNoticiaLabel.setVisibility(View.VISIBLE);
        tvSavedCreadorNoticia.setVisibility(View.VISIBLE);
        tvSavedEnlaceNoticia.setVisibility(View.VISIBLE);
    }
}
