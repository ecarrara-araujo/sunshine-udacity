package eng.ecarrara.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import eng.ecarrara.sunshine.data.WeatherDbHelper;
import eng.ecarrara.sunshine.data.WeatherContract.LocationEntry;
import eng.ecarrara.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by ecarrara on 19/11/2014.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        //Test Data
        String testLocationSetting = "99705";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LONG));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(testCityName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Fantastic.  Now that we have a location, add some weather!
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }

        // Fantastic.  Now that we have a location, add some weather!
        // Weather Test data
        long testLocationKey = locationRowId;
        int testWeatherId = 321;
        String testDateText = "20141205";
        String testShortDesc = "Asteroids";
        String testLongDesc = "Asteroid from Flaming Asteroids Field";
        double testMinTemp = 65;
        double testMaxTemp = 75;
        double testPressure = 1.3;
        double testHumidity = 1.2;
        double testWindspeed = 5.5;
        double testDegrees = 1.1;

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, testDateText);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, testDegrees);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, testHumidity);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, testPressure);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, testMaxTemp);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, testMinTemp);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, testShortDesc);
        weatherValues.put(WeatherEntry.COLUMN_LONG_DESC, testLongDesc);
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, testWindspeed);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, testWeatherId);

        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        // Verification
        assertTrue(weatherRowId != -1);
        Log.i(LOG_TAG, "New weather row id " + weatherRowId);

        // read test
        String[] weatherColumns = {
            WeatherEntry._ID,
            WeatherEntry.COLUMN_LOC_KEY,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_LONG_DESC,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES
        };

        cursor = db.query(
                WeatherEntry.TABLE_NAME,
                weatherColumns,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            int locationKeyIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_LOC_KEY);
            long locationKey = cursor.getLong(locationKeyIndex);

            int weatherIdIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int weatherId = cursor.getInt(weatherIdIndex);

            int dateTextIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
            String dateText = cursor.getString(dateTextIndex);

            int shortDescIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
            String shortDesc = cursor.getString(shortDescIndex);

            int longDescIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_LONG_DESC);
            String longDesc = cursor.getString(longDescIndex);

            int minTempIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
            double minTemp = cursor.getDouble(minTempIndex);

            int maxTempIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
            double maxTemp = cursor.getDouble(maxTempIndex);

            int pressureIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
            double pressure = cursor.getDouble(pressureIndex);

            int humidityIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
            double humidity = cursor.getDouble(humidityIndex);

            int windspeedIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
            double windspeed = cursor.getDouble(windspeedIndex);

            int degreesIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
            double degrees = cursor.getDouble(degreesIndex);

            assertEquals(testLocationKey, locationKey);
            assertEquals(testWeatherId, weatherId);
            assertEquals(testDateText, dateText);
            assertEquals(testShortDesc, shortDesc);
            assertEquals(testLongDesc, longDesc);
            assertEquals(testMinTemp, minTemp);
            assertEquals(testMaxTemp, maxTemp);
            assertEquals(testPressure, pressure);
            assertEquals(testHumidity, humidity);
            assertEquals(testWindspeed, windspeed);
            assertEquals(testDegrees, degrees);

        } else {
            fail("No weather data returned.");
        }

        dbHelper.close();
    }

}
