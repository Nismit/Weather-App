package ca.nismit.util.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import ca.nismit.util.weather.api.WeatherApi;
import ca.nismit.util.weather.forecast.RecyclerAdapter;
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
    private TextView _textLocation;
    private TextView _textDate;
    private TextView _textTemp;
    private CombinedChart _mChart;
    private final int itemcount = 5;
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

        _textLocation = (TextView) findViewById(R.id.ac_textLocation);
        _textDate = (TextView) findViewById(R.id.ac_textDate);
        _textTemp = (TextView) findViewById(R.id.ac_textTemp);
        _mChart = (CombinedChart) findViewById(R.id.ac_combiChart);
        _recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        _recyclerView.setLayoutManager(manager);
        _recyclerView.setHasFixedSize(true);

        //
        // Init chart
        //
        _mChart.getDescription().setEnabled(false);
        //_mChart.setBackgroundColor(R.color.transparent);
        _mChart.setDrawGridBackground(false);
        _mChart.setDrawBarShadow(false);
        _mChart.setHighlightFullBarEnabled(false);
        _mChart.setVisibleXRangeMaximum(5);

        // draw bars behind lines
        _mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = _mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = _mChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawLabels(true);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = _mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        //leftAxis.setDrawLabels(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMinimum(-10f); // this replaces setStartAtZero(true)

        XAxis xAxis = _mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(-0.3f);
        //xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        _mChart.animateY(2500);

        _mChart.setData(data);
        _mChart.invalidate();

        apiInterface = ClientHelper.createService(WeatherApi.class);

        callWeatherApi();
        callForecastApi();
    }

    @Override
    protected void onStart() {
        super.onStart();

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

                // Set date
                setDate(Converter.convertUnixTimetoDate(resource.getDt()));
                //82233213123
                //1489525200
                //1489501517

                // Set temperature into temp text view
                setTemperature(Converter.convertKtoDegree(resource.getMain().getTemp()));
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


                //List<ca.nismit.util.weather.pojoForecast.List> list = resource.getList();
                List<ca.nismit.util.weather.pojoForecast.List> list = resource.getList();
                Log.d(TAG, "callForecastApi List size: " + list.size());
                setRecyclerView(list);
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
        _textTemp.setText(temperature);
    }

    private void setRecyclerView(List<ca.nismit.util.weather.pojoForecast.List> list) {
        _adapter = new RecyclerAdapter(list);
        _recyclerView.setAdapter(_adapter);
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < 4; index++) {
            //entries.add(new Entry(index, getRandom(15, 5)));
            entries.add(new Entry(index, (-2 + index)));
        }

        entries.add(new Entry(4, 18));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int index = 0; index < itemcount; index++) {
            entries1.add(new BarEntry(index, getRandom(25, 25)));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setDrawValues(false);
        //set1.setValueTextColor(Color.rgb(60, 220, 78));
        //set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        float barWidth = 0.20f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

        return d;
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
}
