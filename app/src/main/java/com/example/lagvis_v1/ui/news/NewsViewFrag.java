package com.example.lagvis_v1.ui.news;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint; // ojo: android.text.TextPaint
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lagvis_v1.R;
import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.BaseFragment;
import com.example.lagvis_v1.databinding.FragmentSecondBinding; // <-- ViewBinding
import com.example.lagvis_v1.dominio.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class NewsViewFrag extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentSecondBinding binding; // <-- única referencia a vistas

    private List<NewsItem> listaNoticias;
    private int indiceNoticiaActual = 0;

    private FirebaseAuth auth;
    private NewsViewModel vm;

    public NewsViewFrag() {}

    public static NewsViewFrag newInstance(String param1, String param2) {
        NewsViewFrag fragment = new NewsViewFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        auth = FirebaseAuth.getInstance();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false); // <-- inflate con binding
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Estado inicial UI
        binding.btnAnterior.setEnabled(false);
        binding.btnSiguiente.setEnabled(false);
        binding.btnGuardarNoticia.setEnabled(false);

        // ViewModel
        vm = new ViewModelProvider(this, new NewsViewModelFactory()).get(NewsViewModel.class);

        // Observa noticias
        vm.state.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UiState.Loading) {
                binding.btnAnterior.setEnabled(false);
                binding.btnSiguiente.setEnabled(false);
                binding.btnGuardarNoticia.setEnabled(false);
                binding.tituloNoticia.setText("Cargando noticias…");
                binding.enlaceNoticia.setText("");
                binding.fechaNoticia2.setText("");
                binding.creadorNoticia2.setText("");
            } else if (state instanceof UiState.Success) {
                listaNoticias = ((UiState.Success<List<NewsItem>>) state).data;
                indiceNoticiaActual = 0;
                if (listaNoticias == null || listaNoticias.isEmpty()) {
                    binding.tituloNoticia.setText("No se encontraron noticias.");
                    binding.enlaceNoticia.setText("");
                    binding.fechaNoticia2.setText("");
                    binding.creadorNoticia2.setText("");
                    binding.btnAnterior.setEnabled(false);
                    binding.btnSiguiente.setEnabled(false);
                    binding.btnGuardarNoticia.setEnabled(false);
                } else {
                    mostrarNoticiaActual();
                    actualizarEstadoBotones();
                }
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).message;
                binding.tituloNoticia.setText("Error al cargar las noticias: " + (msg != null ? msg : ""));
                binding.enlaceNoticia.setText("");
                binding.fechaNoticia2.setText("");
                binding.creadorNoticia2.setText("");
                binding.btnAnterior.setEnabled(false);
                binding.btnSiguiente.setEnabled(false);
                binding.btnGuardarNoticia.setEnabled(false);
            }
        });

        // Observa guardado
        vm.save.observe(getViewLifecycleOwner(), s -> {
            if (s instanceof UiState.Loading) {
                binding.btnGuardarNoticia.setEnabled(false);
            } else if (s instanceof UiState.Success) {
                binding.btnGuardarNoticia.setEnabled(true);
                Drawable ok = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle);
                mostrarToastPersonalizado("¡Noticia guardada con éxito!", ok);
            } else if (s instanceof UiState.Error) {
                binding.btnGuardarNoticia.setEnabled(true);
                Drawable err = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline);
                mostrarToastPersonalizado("¡Error al guardar la noticia!", err);
            }
        });

        // Carga inicial
        vm.load("Boe", "es", "business");

        // Listeners
        binding.btnAnterior.setOnClickListener(v -> {
            if (listaNoticias != null && !listaNoticias.isEmpty()) {
                indiceNoticiaActual = Math.max(0, indiceNoticiaActual - 1);
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });
        binding.btnSiguiente.setOnClickListener(v -> {
            if (listaNoticias != null && !listaNoticias.isEmpty()) {
                indiceNoticiaActual = Math.min(listaNoticias.size() - 1, indiceNoticiaActual + 1);
                mostrarNoticiaActual();
                actualizarEstadoBotones();
            }
        });
        binding.btnGuardarNoticia.setOnClickListener(v -> guardarNoticiaActual());
    }

    private void mostrarNoticiaActual() {
        if (binding == null || listaNoticias == null || listaNoticias.isEmpty()) return;
        if (indiceNoticiaActual < 0 || indiceNoticiaActual >= listaNoticias.size()) return;

        NewsItem n = listaNoticias.get(indiceNoticiaActual);

        binding.tituloNoticia.setText(n.title != null ? n.title : "Sin título");
        binding.fechaNoticia2.setText(n.pubDate != null ? n.pubDate : "");
        binding.creadorNoticia2.setText(n.creator != null ? n.creator : "");

        String textoLeerMas = "Pincha aquí para ir al enlace de la noticia!";
        SpannableString spannable = new SpannableString(textoLeerMas);

        ClickableSpan span = new ClickableSpan() {
            @Override public void onClick(@NonNull View widget) {
                if (n.link != null && !n.link.isEmpty()) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(n.link))); }
                    catch (Exception e) { Log.e("INTENT_ERROR", "Error al abrir URL: " + n.link, e); }
                }
            }
            @Override public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        spannable.setSpan(span, 0, textoLeerMas.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.enlaceNoticia.setText(spannable);
        binding.enlaceNoticia.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void actualizarEstadoBotones() {
        if (binding == null) return;
        if (listaNoticias == null || listaNoticias.isEmpty()) {
            binding.btnAnterior.setEnabled(false);
            binding.btnSiguiente.setEnabled(false);
            binding.btnGuardarNoticia.setEnabled(false);
            return;
        }
        binding.btnAnterior.setEnabled(indiceNoticiaActual > 0);
        binding.btnSiguiente.setEnabled(indiceNoticiaActual < listaNoticias.size() - 1);
        binding.btnGuardarNoticia.setEnabled(true);
    }

    private void guardarNoticiaActual() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline);
            mostrarToastPersonalizado("¡Usuario no autenticado!", icon);
            return;
        }
        if (listaNoticias == null || listaNoticias.isEmpty()) {
            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_outline);
            mostrarToastPersonalizado("¡No hay noticia para guardar!", icon);
            return;
        }
        vm.save(currentUser.getUid(), listaNoticias.get(indiceNoticiaActual));
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null; // importante para evitar fugas de memoria
    }
}
