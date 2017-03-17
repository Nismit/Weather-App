package ca.nismit.util.weather.forecast;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.nismit.util.weather.R;
import ca.nismit.util.weather.pojoForecast.Main;
import ca.nismit.util.weather.util.ConvertTemperature;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static String TAG = RecyclerAdapter.class.getSimpleName();
    private LayoutInflater _mInflater;
    private List<ca.nismit.util.weather.pojoForecast.List> _dataList;

    public RecyclerAdapter(final List data) {
        _dataList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _mInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Log.d(TAG, "onBindViewHolder: " + position);
        // Example Data
        String th = getTempAndHumidity(position);
        holder._tempAndHumidity.setText(th);
        holder._time.setText("13:00");
        //Log.d(TAG, "onBindViewHolder: " + _dataList.get(position).getDt());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: " + _dataList.size());
        if (_dataList != null) {
            return _dataList.size();
        } else {
            return 0;
        }
    }

    private String getTempAndHumidity(int position) {
        String result;
        Main tempData = _dataList.get(position).getMain();
        String temp = ConvertTemperature.convertKtoDegree(tempData.getTemp());
        String humidity = Integer.toString(tempData.getHumidity());
        result = temp + "° / " + humidity + "%";
        return result;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _weatherIcon;
        TextView _tempAndHumidity, _time;

        public ViewHolder(View itemView) {
            super(itemView);
            _weatherIcon = (ImageView) itemView.findViewById(R.id.fi_weather_icon);
            _tempAndHumidity = (TextView) itemView.findViewById(R.id.fi_temp_humidity);
            _time = (TextView) itemView.findViewById(R.id.fi_time);
        }
    }
}
