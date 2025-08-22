// data/remote/NewsApi.java
package com.example.lagvis_v1.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    // Base URL: https://newsdata.io/api/1/
    @GET("news")
    Call<NewsResponseDto> getNews(
            @Query("apikey") String apiKey,     // o quítalo si usas interceptor (ver abajo)
            @Query("q") String query,
            @Query("country") String country,
            @Query("category") String category
    );
}
