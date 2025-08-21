// core/network/RetrofitProvider.java
package com.example.lagvis_v1.core.network;

import com.example.lagvis_v1.data.remote.NewsApi;
import com.example.lagvis_v1.data.remote.HolidaysApi;
import com.example.lagvis_v1.data.remote.ProfileApi;
import com.example.lagvis_v1.data.remote.RatingsApi;
import com.example.lagvis_v1.data.remote.SaveNewsApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetroFitProvider {
    private RetroFitProvider(){}

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

    public static HolidaysApi provideHolidaysApi() {
        return new Retrofit.Builder()
                .baseUrl("https://date.nager.at/api/v3/") // Nager.Date
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HolidaysApi.class);
    }
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
}
