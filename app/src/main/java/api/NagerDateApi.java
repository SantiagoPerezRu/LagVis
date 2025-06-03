package api;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NagerDateApi {
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    Call<List<PublicHoliday>> getPublicHolidays(
            @Path("year") int year,
            @Path("countryCode") String countryCode,
            @Query("countyCode") String countyCode
    );
}
