// core/network/RetroFitProvider.java
package com.example.lagvis_v1.core.network;

import com.example.lagvis_v1.data.remote.ProfileApi;
import com.example.lagvis_v1.data.remote.RatingsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetroFitProvider {
    private RetroFitProvider(){}


    public static ProfileApi provideProfileApi() {
        // Base dummy válida; con @Url dinámico se ignora
        return new Retrofit.Builder()
                .baseUrl("https://lagvis.invalid/")
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
