package ca.nismit.util.weather.util;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

    public static String convertKtoDegree(Double temp) {
        double roundTemp = (temp - 273.15f);
        BigDecimal bd = new BigDecimal(roundTemp);
        String result = bd.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        return result + "°";
    }

    public static String convertUnixTimetoDate(@Nullable String resultType, Long millisec) {
        SimpleDateFormat format;
        String result;
        switch (resultType) {
            case "TIME" :
                format = new SimpleDateFormat("HH:mm");
                 result = format.format(new Date(millisec * 1000));
                return result;
            default:
                format = new SimpleDateFormat("EEEE MMMM d, yyyy");
                result = format.format(new Date(millisec * 1000));
                return result;
        }
    }
}
