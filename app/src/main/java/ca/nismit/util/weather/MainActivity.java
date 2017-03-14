package ca.nismit.util.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import ca.nismit.util.weather.api.WeatherApi;
import ca.nismit.util.weather.pojoForecast.WeatherForecastResponse;
import ca.nismit.util.weather.pojoWeather.WeatherResponse;
import ca.nismit.util.weather.util.ClientHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private static String WEATHER_PATH_URL = "data/2.5/weather";
    private static String FORECAST_PATH_URL = "data/2.5/forecast";

    private WeatherApi apiInterface;
    private TextView _textLocation;
    private TextView _textDate;
    private TextView _textTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        // Transparent Status bar with no Action bar
        //
        findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_main);

        _textLocation = (TextView) findViewById(R.id.ac_textLocation);
        _textDate = (TextView) findViewById(R.id.ac_textDate);
        _textTemp = (TextView) findViewById(R.id.ac_textTemp);

        apiInterface = ClientHelper.createService(WeatherApi.class);

        callWeatherApi();
        callForecastApi();
    }

    private void callWeatherApi() {
        Call<WeatherResponse> call = apiInterface.getWeatherWithCityID(WEATHER_PATH_URL, "6173331", BuildConfig.OWM_API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "callWeatherApi/Response Code: " + response.code());
                WeatherResponse resource = response.body();

                // Set location into location text view
                setLocation(resource.getName() + ", " + resource.getSys().getCountry());

                // round up temperature and convert to degree from kelvin
                double roundTemp = (resource.getMain().getTemp() - 273.15f);
                BigDecimal bd = new BigDecimal(roundTemp);
                String temp = bd.setScale(0, BigDecimal.ROUND_HALF_UP).toString();

                // Set temperature into temp text view
                setTemperature(temp);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.toString());
                call.cancel();
            }
        });
    }

    private void callForecastApi() {
        Call<WeatherForecastResponse> call = apiInterface.getForecastWithCityID(FORECAST_PATH_URL, "6173331", BuildConfig.OWM_API_KEY);
        call.enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                Log.d(TAG, "callForecastApi/Response Code: " + response.code());
                WeatherForecastResponse resource = response.body();
                
                List<ca.nismit.util.weather.pojoForecast.List> list = resource.getList();
                //Log.d(TAG, "#####onResponse: " + list.size());
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.toString());
                call.cancel();
            }
        });
    }

    private void setLocation(String location) {
        _textLocation.setText(location);
    }

    private void setDate(String date) {
        _textDate.setText(date);
    }

    private void setTemperature(String temperature) {
        _textTemp.setText(temperature + "°");
    }
}
