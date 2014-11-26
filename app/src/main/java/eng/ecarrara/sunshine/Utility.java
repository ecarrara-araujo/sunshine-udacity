package eng.ecarrara.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

import eng.ecarrara.sunshine.data.WeatherContract;

/**
 * Created by ecarrara on 26/11/2014.
 */
public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_temperature_unit_key),
                context.getString(R.string.pref_temperature_unit_value_metric))
                .equals(context.getString(R.string.pref_temperature_unit_value_metric));
    }

    /**
     * Converts the temperature in celsius (metric) to fahrenheit (imperial)
     * @param temperature
     * @return
     */
    public static double convertTemperatureFromMetricToImperial(double temperature) {
        double imperialTemp = (temperature * 9) / 5 + 32;
        imperialTemp = Math.round(imperialTemp);
        return imperialTemp;
    }

    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = convertTemperatureFromMetricToImperial(temperature);
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
}
