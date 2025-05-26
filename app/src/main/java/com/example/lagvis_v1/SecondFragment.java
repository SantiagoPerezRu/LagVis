package com.example.lagvis_v1;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import api.NewsApiService;
import api.NewsItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.graphics.Color;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondFragment extends BaseFragment implements NewsApiService.NoticiasCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tituloNoticiaTextView;
    private TextView enlaceNoticiaTextView;
    private List<NewsItem> listaNoticias;
    private int indiceNoticiaActual = 0;
    private Button btnAnterior;
    private Button btnSiguiente;

    private Button btnGuardarNoticia;
    private TextView verMasTexto;

    private TextView fechaNoticia;

    private TextView creadorNoticia;

    private FirebaseAuth auth;

    private String uidUsuario;

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tituloNoticiaTextView = view.findViewById(R.id.tituloNoticia);
        enlaceNoticiaTextView = view.findViewById(R.id.enlaceNoticia);
        btnAnterior = view.findViewById(R.id.btnAnterior);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);
        //verMasTexto = view.findViewById(R.id.verMasTaexto);
        fechaNoticia = view.findViewById(R.id.fechaNoticia2);
        creadorNoticia = view.findViewById(R.id.creadorNoticia2);
        btnGuardarNoticia = view.findViewById(R.id.btnGuardarNoticia);
        auth = FirebaseAuth.getInstance();

        // Deshabilitar botones al inicio
        btnAnterior.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnGuardarNoticia.setEnabled(false);

        NewsApiService newsApiService = new NewsApiService(this);
        newsApiService.obtenerNoticias("Boe", "es", "business");

        btnAnterior.setOnClickListener(v -> {
            if (listaNoticias != null && !listaNoticias.isEmpty()) {
                indiceNoticiaActual--;
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (listaNoticias != null && !listaNoticias.isEmpty()) {
                indiceNoticiaActual++;
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });

        btnGuardarNoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarNoticiaActual();
            }
        });

        return view;
    }

    @Override
    public void onNoticiasObtenidas(List<NewsItem> noticias) {
        listaNoticias = noticias;
        indiceNoticiaActual = 0;

        if (tituloNoticiaTextView != null && enlaceNoticiaTextView != null) {
            if (noticias.isEmpty()) {
                tituloNoticiaTextView.setText("No se encontraron noticias.");
                enlaceNoticiaTextView.setText("");
                btnAnterior.setEnabled(false);
                btnSiguiente.setEnabled(false);
                verMasTexto.setVisibility(View.GONE);
            } else {
                mostrarNoticiaActual();
                actualizarEstadoBotones();
               //verMasTexto.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNoticiasError(String error) {
        if (tituloNoticiaTextView != null && enlaceNoticiaTextView != null) {
            tituloNoticiaTextView.setText("Error al cargar las noticias: " + error);
            enlaceNoticiaTextView.setText("");
            btnAnterior.setEnabled(false);
            btnSiguiente.setEnabled(false);
            verMasTexto.setVisibility(View.GONE);
        }
    }

    private void guardarNoticiaActual() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Drawable checkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
            mostrarToastPersonalizado("¡Usuario no autenticado!", checkIcon);
            return;
        }
        String uid = currentUser.getUid();

        if (listaNoticias != null && !listaNoticias.isEmpty()) {
            NewsItem noticia = listaNoticias.get(indiceNoticiaActual);
            String titulo = noticia.title;
            String fecha = noticia.pubDate;
            String enlace = noticia.link;
            String creador = noticia.creator;
            guardarNoticiaEnServidorVolley(uid, titulo, fecha, enlace, creador);
        } else {
            Drawable checkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
            mostrarToastPersonalizado("¡No hay noticia para guardar!", checkIcon);
        }
    }


    private void mostrarNoticiaActual() {
        if (listaNoticias != null && !listaNoticias.isEmpty()) {
            if (indiceNoticiaActual >= 0 && indiceNoticiaActual < listaNoticias.size()) {
                NewsItem noticia = listaNoticias.get(indiceNoticiaActual);
                tituloNoticiaTextView.setText(noticia.title);

                fechaNoticia.setText(noticia.pubDate);
                creadorNoticia.setText(noticia.creator);


                String textoLeerMas = "Pincha aquí para ir a la enlace de la noticia!";

                SpannableString spannableString = new SpannableString(textoLeerMas);

                // Crear un ClickableSpan para abrir la URL
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.link));
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("INTENT_ERROR", "Error al abrir la URL: " + noticia.link, e);
                            // Muestra un mensaje al usuario (opcional)
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.BLUE); // Color del enlace
                        ds.setUnderlineText(true); // Subrayar el enlace
                    }
                };

                // Aplicar el ClickableSpan al texto "Pincha aquí"
                spannableString.setSpan(clickableSpan, 0, 11, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE); // 0, 10 es la posición de "Pincha aquí"
                enlaceNoticiaTextView.setText(spannableString);
                enlaceNoticiaTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Necesario para que el ClickableSpan funcione
            }
        }
    }

    private void actualizarEstadoBotones() {
        if (listaNoticias == null || listaNoticias.isEmpty()) {
            btnAnterior.setEnabled(false);
            btnSiguiente.setEnabled(false);
        } else if (indiceNoticiaActual == 0) {
            btnAnterior.setEnabled(false);
            btnSiguiente.setEnabled(true);
            btnGuardarNoticia.setEnabled(true);
        } else if (indiceNoticiaActual == listaNoticias.size() - 1) {
            btnSiguiente.setEnabled(false);
            btnAnterior.setEnabled(true);
            btnGuardarNoticia.setEnabled(true);
        } else {
            btnAnterior.setEnabled(true);
            btnSiguiente.setEnabled(true);
            btnGuardarNoticia.setEnabled(true);
        }
    }

    private void guardarNoticiaEnServidorVolley(String uid, String titulo, String fecha, String enlace, String creador) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Guardando noticia...");
        progressDialog.show();

        String url = LagVisConstantes.ENDPOINT_GUARDAR_NOTICIA;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Drawable checkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_circle);
                        mostrarToastPersonalizado("¡Noticia guardada con éxito!", checkIcon);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Drawable checkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_outline);
                        mostrarToastPersonalizado("¡Error al guarda la noticia!", checkIcon);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("titulo", titulo);
                params.put("fecha", fecha);
                params.put("enlace", enlace);
                params.put("creador", creador);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

}

