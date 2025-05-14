package VisualizadorPaginas;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lagvis_v1.R;

public class ActivityPaginaVidaLaboral extends AppCompatActivity {

    private WebView webViewNavegador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_vida_laboral); // Asegúrate de que el nombre del layout coincida

        webViewNavegador = findViewById(R.id.webViewNavegador);

        // Habilita Javascript (necesario para la mayoría de los sitios web)
        webViewNavegador.getSettings().setJavaScriptEnabled(true);

        // Evita que los enlaces abran el navegador externo
        webViewNavegador.setWebViewClient(new WebViewClient());

        // Carga la URL del informe de vida laboral
        webViewNavegador.loadUrl("https://portal.seg-social.gob.es/wps/portal/importass/importass/Categorias/Vida+laboral+e+informes/Informes+sobre+tu+situacion+laboral/Informe+de+tu+vida+laboral");
    }

    // Opcional: Manejar el botón de "atrás" para navegar dentro del WebView
    @Override
    public void onBackPressed() {
        if (webViewNavegador.canGoBack()) {
            webViewNavegador.goBack();
        } else {
            super.onBackPressed();
        }
    }
}