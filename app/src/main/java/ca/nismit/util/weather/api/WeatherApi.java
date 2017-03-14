package ca.nismit.util.weather.api;

import ca.nismit.util.weather.pojoForecast.WeatherForecastResponse;
import ca.nismit.util.weather.pojoWeather.WeatherResponse;
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

    @GET("/{urlPath}")
    Call<WeatherResponse> getWeatherWithGPS(
            @Path("urlPath") String urlPath,
            @Query("lat") float lat,
            @Query("lon") float lon,
            @Query("appid") String appId
    );

    @GET("/{urlPath}")
    Call<WeatherForecastResponse> getForecastWithCityID(
            @Path("urlPath") String urlPath,
            @Query("id") String cityId,
            @Query("appid") String appId
    );

    @GET("/{urlPath}")
    Call<WeatherForecastResponse> getForecastWithGPS(
            @Path("urlPath") String urlPath,
            @Query("lat") float lat,
            @Query("lon") float lon,
            @Query("appid") String appId
    );
}
