package ca.nismit.util.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import ca.nismit.util.weather.api.WeatherApi;
import ca.nismit.util.weather.forecast.RecyclerAdapter;
import ca.nismit.util.weather.marker.CustomMarkerView;
import ca.nismit.util.weather.pojoForecast.WeatherForecastResponse;
import ca.nismit.util.weather.pojoWeather.WeatherResponse;
import ca.nismit.util.weather.util.ClientHelper;
import ca.nismit.util.weather.util.Converter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private static String WEATHER_PATH_URL = "data/2.5/weather";
    private static String FORECAST_PATH_URL = "data/2.5/forecast";

    private WeatherApi apiInterface;
    private RelativeLayout _relativeLayout;
    private ImageView _weatherIcon;
    private TextView _textLocation, _textDate, _textTemp, _textSunrise, _textSunset, _textHumidity, _textWind;
    private CombinedChart _mChart;
    private CustomMarkerView _markerView;
    private XAxis _xAxis;
    private final int itemcount = 12;
    private RecyclerView _recyclerView;
    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager _lManager;

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

        // Init relative view
        _relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        //
        // Init text view
        //
        _textLocation = (TextView) findViewById(R.id.ac_textLocation);
        _textDate = (TextView) findViewById(R.id.ac_textDate);
        _textTemp = (TextView) findViewById(R.id.ac_textTemp);
        _textSunrise = (TextView) findViewById(R.id.ac_text_sunrise);
        _textSunset = (TextView) findViewById(R.id.ac_text_sunset);
        _textHumidity = (TextView) findViewById(R.id.ac_text_humidity);
        _textWind = (TextView) findViewById(R.id.ac_text_wind);

        // Init image view
        _weatherIcon = (ImageView) findViewById(R.id.ac_weather_icon);

        // Init Chart
        _mChart = (CombinedChart) findViewById(R.id.ac_combiChart);
        // Init Recycler view
        _recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        _recyclerView.setLayoutManager(manager);
        _recyclerView.setHasFixedSize(true);

        apiInterface = ClientHelper.createService(WeatherApi.class);
        callWeatherApi();
        callForecastApi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        initChart();
    }

    private void callWeatherApi() {
        Call<WeatherResponse> call = apiInterface.getWeatherWithCityID(WEATHER_PATH_URL, "6173331", BuildConfig.OWM_API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "callWeatherApi/Response Code: " + response.code());
                WeatherResponse resource = response.body();

                Log.d(TAG, "onResponse: "+ resource.getWeather().get(0).getId());

                setBackground(resource.getWeather().get(0).getId());

                // Set location into location text view
                setLocation(resource.getName() + ", " + resource.getSys().getCountry());

                // Set date
                setDate(Converter.convertUnixTimetoDate("DATE", resource.getDt()));
                //Log.d(TAG, "onResponse: " + Converter.convertUnixTimetoDate("TIME", resource.getDt()));

                // Set icon
                setWeatherIcon(resource.getWeather().get(0).getIcon());

                // Set temperature into temp text view
                setTemperature(Converter.convertKtoDegree(resource.getMain().getTemp()));

                // Set sun rise time
                setSunrise(Converter.convertUnixTimetoDate("TIME", resource.getSys().getSunrise()));

                // Set sun rise time
                setSunset(Converter.convertUnixTimetoDate("TIME", resource.getSys().getSunset()));

                // Set humidity
                setHumidity(Integer.toString(resource.getMain().getHumidity()));

                // Set wind
                setWind(Double.toString(resource.getWind().getSpeed()));
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
                Log.d(TAG, "callForecastApi List size: " + list.size());
                drawChart(list);
                setRecyclerView(list);
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.toString());
                call.cancel();
            }
        });
    }

    private void setBackground(int weatherId) {
        String weather = null;
        if(weatherId >= 200 && weatherId < 300) {
            weather = "THUNDERSTORM";
        } else if(weatherId >= 300 && weatherId < 400) {
            weather = "DRIZZLE";
        } else if(weatherId >= 500 && weatherId < 600) {
            weather = "RAIN";
        } else if(weatherId >= 600 && weatherId < 700) {
            weather = "SNOW";
        } else if(weatherId >= 700 && weatherId < 800) {
            weather = "CLOUD";
        } else if(weatherId == 800) {
            weather = "SUN";
        } else if(weatherId >= 801 && weatherId < 900) {
            weather = "CLOUD";
        }

        switch (weather) {
            case "SUN" :
                _relativeLayout.setBackgroundResource(R.drawable.gradation_sunny);
                break;
            case "CLOUD" :
                _relativeLayout.setBackgroundResource(R.drawable.gradation_cloud);
                break;
            case "RAIN" :
                _relativeLayout.setBackgroundResource(R.drawable.gradation_rain);
                break;
            case "SNOW" :
                _relativeLayout.setBackgroundResource(R.drawable.gradation_snow);
                break;
            default:
                _relativeLayout.setBackgroundResource(R.drawable.gradation_sunny);
                break;
        }

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

    private void setSunrise(String time) {
        _textSunrise.setText(time);
    }

    private void setSunset(String time) {
        _textSunset.setText(time);
    }

    private void setHumidity(String humidity) {
        _textHumidity.setText(humidity + "%");
    }

    private void setWind(String wind) {
        _textWind.setText(wind + " m/s");
    }

    private void setWeatherIcon(String iconNo) {
        iconNo = "ic_" + iconNo;
        int resourceId = getResources().getIdentifier(iconNo, "drawable", "ca.nismit.util.weather");
        _weatherIcon.setImageResource(resourceId);
    }

    private void setRecyclerView(List<ca.nismit.util.weather.pojoForecast.List> list) {
        _adapter = new RecyclerAdapter(this, list);
        _recyclerView.setAdapter(_adapter);
    }

    /**
     * Init chart
     * In this case, the graph will be painted combine data.
     */
    private void initChart() {
        _markerView = new CustomMarkerView(this, R.layout.custom_marker);
        _mChart.setMarker(_markerView);
        _mChart.getDescription().setEnabled(false);
        _mChart.setDrawGridBackground(false);
        _mChart.setDrawBarShadow(false);
        _mChart.setDoubleTapToZoomEnabled(false);

        // draw bars behind lines
        _mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        setLegend();
        setSideAxis();
        setXAxis();
    }

    /**
     * Init legend in chart
     */
    private void setLegend() {
        Legend l = _mChart.getLegend();
        l.setEnabled(false);
    }

    /**
     * Set Y axis
     */
    private void setSideAxis() {
        // Right
        YAxis rightAxis = _mChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisLineColor(Color.WHITE);
        //rightAxis.setDrawLabels(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setGranularity(1f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = _mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        //leftAxis.setDrawLabels(true);
        //leftAxis.setAxisMinimum(-5f); // this replaces setStartAtZero(true)
    }

    private void setXAxis() {
        _xAxis = _mChart.getXAxis();
        _xAxis.setTextColor(Color.WHITE);
        _xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        _xAxis.setAxisLineColor(Color.WHITE);
        _xAxis.setDrawGridLines(false);
        //xAxis.setCenterAxisLabels(true);
        _xAxis.setAxisMinimum(-0.3f);
        _xAxis.setGranularity(1f);
    }

    private void drawChart(final List<ca.nismit.util.weather.pojoForecast.List> list) {
        CombinedData data = new CombinedData();

        data.setData(generateLineData(getTempData(list)));
        data.setData(generateBarData(getRainData(list)));

        _xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //Log.d(TAG, "getFormattedValue: " + value);
                String result = Converter.convertUnixTimetoDate("TIME", list.get((int) value % list.size()).getDt());
                return result;
            }


        });

        _xAxis.setAxisMaximum(data.getXMax() + 0.3f);
        _mChart.setVisibleXRangeMaximum(4);
        _mChart.setPinchZoom(false);
        _mChart.setVisibleYRangeMaximum((data.getYMax()+ 2f), YAxis.AxisDependency.LEFT);

        _mChart.animateY(2500);
        _mChart.setData(data);
        _mChart.invalidate();
    }

    private LineData generateLineData(ArrayList<Entry> entries) {
        LineDataSet set = new LineDataSet(entries, "Temp");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(252, 238, 33));
        set.setCircleRadius(7f);
        set.setFillColor(Color.rgb(252, 238, 33));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //set.setDrawValues(true);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData d = new LineData();
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(ArrayList<BarEntry> entries) {
        BarDataSet set = new BarDataSet(entries, "Rain");
        set.setColor(Color.rgb(235, 255, 255));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        float barWidth = 0.5f;
        BarData d = new BarData(set);
        d.setBarWidth(barWidth);

        return d;
    }

    private ArrayList<Entry> getTempData(List<ca.nismit.util.weather.pojoForecast.List> list) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        //for (int index = 0; index < list.size(); index++) {
        for (int index = 0; index < itemcount; index++) {
            Float temp = Converter.castFloatDegree(list.get(index).getMain().getTemp());
            entries.add(new BarEntry(index, temp));
        }

        return entries;
    }

    private ArrayList<BarEntry> getRainData(List<ca.nismit.util.weather.pojoForecast.List> list) {
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        //for (int index = 0; index < list.size(); index++) {
        for (int index = 0; index < itemcount; index++) {
            Float rain = Converter.castDoubletoFloat(list.get(index).getRain().get3h());
            entries.add(new BarEntry(index, rain));
        }

        return entries;
    }
}
