package ca.nismit.util.weather.util;

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

    public static String convertUnixTimetoDate(Long millisec) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMMM d, yyyy");
        String result = format.format(new Date(millisec * 1000));
        return result;
    }
}
