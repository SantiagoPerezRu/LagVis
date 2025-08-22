// com/example/lagvis_v1/data/remote/HolidaysApi.java
package com.example.lagvis_v1.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

// DTOs de red (abajo)
public interface HolidaysApi {
    @GET("/v1/holidays")
    Call<HolidaysResponse> getHolidays(
            @Query("slug") String slug,
            @Query("year") int year

    );
}
