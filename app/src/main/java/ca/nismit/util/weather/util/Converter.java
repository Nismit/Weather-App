package ca.nismit.util.weather.util;

import java.math.BigDecimal;

public class Converter {

    public static String convertKtoDegree(Double temp) {
        double roundTemp = (temp - 273.15f);
        BigDecimal bd = new BigDecimal(roundTemp);
        String result = bd.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        return result + "°";
    }
}
