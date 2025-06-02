package VisualizadorPaginas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lagvis_v1.R;

public class PaginaVidaLaboralFragment extends Fragment {

    private WebView webViewNavegador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagina_vida_laboral, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        webViewNavegador = view.findViewById(R.id.webViewNavegador);

        webViewNavegador.getSettings().setJavaScriptEnabled(true);
        webViewNavegador.setWebViewClient(new WebViewClient());

        webViewNavegador.loadUrl("https://portal.seg-social.gob.es/wps/portal/importass/importass/Categorias/Vida+laboral+e+informes/Informes+sobre+tu+situacion+laboral/Informe+de+tu+vida+laboral");
    }

    // Método para manejar el "volver atrás" si el contenedor lo permite
    public boolean puedeIrAtras() {
        return webViewNavegador != null && webViewNavegador.canGoBack();
    }

    public void irAtras() {
        if (puedeIrAtras()) {
            webViewNavegador.goBack();
        }
    }
}
