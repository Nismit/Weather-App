package ca.nismit.util.weather.api;

import ca.nismit.util.weather.pojo.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("/{urlPath}")
    Call<WeatherResponse> getWeatherWithCityID(
            @Path("urlPath") String urlPath,
            @Query("id") String cityId,
            @Query("appid") String appId
    );
}
