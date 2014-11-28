package eng.ecarrara.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eng.ecarrara.sunshine.data.WeatherContract;
import eng.ecarrara.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by ecarrara on 26/11/2014.
 */
public class ForecastDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ForecastDetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private static final int DETAIL_LOADER = 0;
    private static final String LOCATION_KEY = "location";

    public static final String DATE_KEY = "forecast_date";

    private CursorLoader mForecastLoader;
    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;
    private String mForecast;
    private String mLocation;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_LONG_DESC,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_LONG_DESC = 5;
    public static final int COL_WEATHER_HUMIDITY = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_PRESSURE = 8;
    public static final int COL_WEATHER_DEGREES = 9;
    public static final int COL_WEATHER_WEATHER_ID = 10;

    private ImageView mIconView;
    private TextView mDayOfWeekView;
    private TextView mDateView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mWeatherDescriptionView;
    private TextView mHumidityView;
    private TextView mWindSpeedView;
    private TextView mPressureView;

    public ForecastDetailFragment() { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if( arguments != null && getArguments().containsKey(DATE_KEY)
                && mLocation != null
                && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconView = (ImageView) rootView.findViewById(R.id.detail_weather_icon);
        mDayOfWeekView = (TextView) rootView.findViewById(R.id.detail_day_of_weeek_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mWeatherDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindSpeedView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "In onCreateLoader");
        String forecastDate = getArguments().getString(DATE_KEY);
        mLocation = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(
                mLocation, forecastDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if(!cursor.moveToFirst()) { return; }

        String dayOfWeekString = Utility.getDayName(getActivity(),
                cursor.getString(COL_WEATHER_DATE));
        String dateString = Utility.getFormattedMonthDay(getActivity(),
                cursor.getString(COL_WEATHER_DATE));

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(),
                cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String min = Utility.formatTemperature(getActivity(),
                cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        int weatherId =
                Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(COL_WEATHER_WEATHER_ID));
        String weatherDescription = cursor.getString(COL_WEATHER_DESC);

        String humidityString = String.format(getActivity().getString(R.string.format_humidity),
                cursor.getFloat(COL_WEATHER_HUMIDITY));
        String windString = Utility.getFormattedWind(getActivity(),
                cursor.getFloat(COL_WEATHER_WIND_SPEED), cursor.getFloat(COL_WEATHER_DEGREES));
        String pressureString = String.format(getActivity().getString(R.string.format_pressure),
                cursor.getFloat(COL_WEATHER_PRESSURE));

        mDayOfWeekView.setText(dayOfWeekString);
        mDateView.setText(dateString);
        mHighView.setText(high);
        mLowView.setText(min);
        mIconView.setImageResource(weatherId);
        mWeatherDescriptionView.setText(weatherDescription);
        mHumidityView.setText(humidityString);
        mWindSpeedView.setText(windString);
        mPressureView.setText(pressureString);

        // We still need this for the share intent
        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, min);

        Log.v(LOG_TAG, "Forecast String: " + mForecast);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }
}
