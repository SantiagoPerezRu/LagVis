// com/example/lagvis_v1/data/remote/HolidaysApi.java
package com.example.lagvis_v1.data.remote;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HolidaysApi {
    // GET https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}
    @GET("PublicHolidays/{year}/{country}")
    Call<List<PublicHolidayDto>> getHolidays(
            @Path("year") int year,
            @Path("country") String countryCode
    );
}
