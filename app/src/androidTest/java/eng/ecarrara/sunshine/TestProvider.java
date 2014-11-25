package eng.ecarrara.sunshine;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import eng.ecarrara.sunshine.data.WeatherContract.LocationEntry;
import eng.ecarrara.sunshine.data.WeatherContract.WeatherEntry;
import eng.ecarrara.sunshine.data.WeatherDbHelper;

/**
 * Created by ecarrara on 19/11/2014.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testInsertReadDb() {

        //Test Data

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of locationValues, where column names are the keys
        ContentValues locationValues = TestDb.createNorthPoleLocationValues();
        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, locationValues);

        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, locationValues);

        // Fantastic.  Now that we have a location, add some weather!
        // Weather Test data
        ContentValues weatherValues = TestDb.createWeatherValues(locationRowId);
        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        // Verification
        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New weather row id " + weatherRowId);

        // read test
        cursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        TestDb.validateCursor(cursor, weatherValues);

        // Add the location locationValues in with the weather data so that we can make
        // sure that the join worked and we actually get all the locationValues back
        addAllContentValues(weatherValues, locationValues);

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestDb.TEST_LOCATION),
                null,
                null,
                null,
                null);
        TestDb.validateCursor(cursor, weatherValues);

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                null,
                null,
                null,
                null);
        TestDb.validateCursor(cursor, weatherValues);

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                null,
                null,
                null,
                null);

        cursor.close();
        dbHelper.close();
    }

    public void testGetType() throws Throwable {
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String singleTestDate = "20140612";
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, singleTestDate));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }
}
