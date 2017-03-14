package ca.nismit.util.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import ca.nismit.util.weather.api.WeatherApi;
import ca.nismit.util.weather.pojo.Weather;
import ca.nismit.util.weather.pojo.WeatherResponse;
import ca.nismit.util.weather.util.ClientHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private static String PATH_URL = "data/2.5/weather";

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

        WeatherApi apiInterface = ClientHelper.createService(WeatherApi.class);

        Call<WeatherResponse> call = apiInterface.getWeatherWithCityID(PATH_URL, "6173331", BuildConfig.OWM_API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "Response Code: " + response.code());
                WeatherResponse resource = response.body();

                _textLocation.setText(resource.getName());

                double roundTemp = (resource.getMain().getTemp() - 273.15f);
                BigDecimal bd = new BigDecimal(roundTemp);
                String temp = bd.setScale(0, BigDecimal.ROUND_HALF_UP).toString();

                _textTemp.setText(temp + "°");

                List<Weather> weather = resource.getWeather();
                Log.d(TAG, "DESC: " + weather.get(0).getDescription());


            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.toString());
                call.cancel();
            }
        });

    }
}
