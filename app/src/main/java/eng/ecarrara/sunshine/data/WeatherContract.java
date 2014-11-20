package eng.ecarrara.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by ecarrara on 19/11/2014.
 * Defines tables and column names for the weather database
 */
public class WeatherContract {

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as a Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear"
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_LONG_DESC = "long_desc";

        // Min and Max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure stored as float representing the barometric value
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as float representing windspeed mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south). Stored as floats
        public static final String COLUMN_DEGREES = "degrees";

    }

    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location_entry";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // The city name stored as String
        public static final String COLUMN_CITY_NAME = "city_name";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

    }



}
