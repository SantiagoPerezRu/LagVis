// core/network/RetroFitProvider.java
package com.example.lagvis_v1.core.network;

import com.example.lagvis_v1.core.util.LagVisConstantes;
import com.example.lagvis_v1.data.remote.NewsApi;
import com.example.lagvis_v1.data.remote.HolidaysApi;
import com.example.lagvis_v1.data.remote.ProfileApi;
import com.example.lagvis_v1.data.remote.RatingsApi;
import com.example.lagvis_v1.data.remote.SaveNewsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetroFitProvider {
    private RetroFitProvider() {
    }

    public static NewsApi provideNewsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsdata.io/api/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(NewsApi.class);
    }

    public static SaveNewsApi provideSaveNewsApi() {
        // Base dummy válida; se ignora cuando uses @Url en la llamada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lagvis.invalid/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SaveNewsApi.class);
    }

    /**
     * API de CALENDARIO (Spring Boot propio)
     * - Por defecto apunta a tu backend local desde el emulador Android.
     * Usa http://10.0.2.2:8080/ (loopback del host)
     */
    public static HolidaysApi provideHolidaysApi() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.1.167:3000/") // <-- NUEVO: tu Spring Boot
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HolidaysApi.class);
    }

    /**
     * Variante para usar otra baseUrl (por ejemplo, cuando lo despliegues en un servidor).
     * Ej: provideHolidaysApi("https://mi-backend.com/")
     */
    public static HolidaysApi provideHolidaysApi(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(ensureEndsWithSlash(baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HolidaysApi.class);
    }

    public static ProfileApi provideProfileApi() {
        // Base dummy válida; con @Url dinámico se ignora
        return new Retrofit.Builder()
                .baseUrl("http://83.33.98.244/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProfileApi.class);
    }

    public static RatingsApi provideRatingsApi() {
        return new Retrofit.Builder()
                .baseUrl("https://lagvis.invalid/") // se ignora por usar @Url dinámico
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RatingsApi.class);
    }

    // Utilidad pequeña para evitar errores si te olvidas la barra final
    private static String ensureEndsWithSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }
}
