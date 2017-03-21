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
        return result;
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

    public static float castFloatDegree(Double temp) {
        float result;
        double roundTemp = (temp - 273.15f);
        BigDecimal bd = new BigDecimal(roundTemp);
        result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return result;
    }

    public static float castDoubletoFloat(Double value) {
        float result;
        if (value != null) {
            result = value.floatValue();
            return result;
        } else {
            return 0;
        }
    }
}
