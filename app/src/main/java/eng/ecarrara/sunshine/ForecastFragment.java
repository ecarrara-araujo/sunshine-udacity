package eng.ecarrara.sunshine;

/**
 * Created by ecarrara on 7/30/2014.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import eng.ecarrara.sunshine.data.WeatherContract;
import eng.ecarrara.sunshine.data.WeatherContract.LocationEntry;
import eng.ecarrara.sunshine.data.WeatherContract.WeatherEntry;

/**
 * A fragment containing the weather forecast view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private SimpleCursorAdapter mForecastDataAdapter;

    private static final int FORECAST_LOADER = 0;
    private String mLocation;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_LONG_DESC,
            LocationEntry.COLUMN_LOCATION_SETTING,

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_LONG_DESC = 5;
    public static final int COL_LOCATION_SETTING = 6;

    public ForecastFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mForecastDataAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                // these column name
                new String[] {WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP
                },
                new int[] {R.id.list_item_date_textview,
                    R.id.list_item_forecast_textview,
                    R.id.list_item_high_textview,
                    R.id.list_item_low_textview
                }, 0);

        mForecastDataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean isMetric = Utility.isMetric(getActivity());
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP: {
                        ((TextView) view).setText(Utility.formatTemperature(
                                cursor.getDouble(columnIndex), isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String dateString = cursor.getString(columnIndex);
                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(dateString));
                        return true;
                    }
                }
                return false;
            }

        });

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView weatherListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        weatherListView.setAdapter(mForecastDataAdapter);
        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
                Cursor forecastCursor = adapter.getCursor();
                String forecast = "";

                if(forecastCursor.moveToPosition(position)) {
                    boolean isMetric = Utility.isMetric(getActivity());
                    String dateString = Utility.formatDate(
                            forecastCursor.getString(COL_WEATHER_DATE));
                    String forecastString = forecastCursor.getString(COL_WEATHER_DESC);
                    String tempHigh = Utility.formatTemperature(
                            forecastCursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                    String tempLow = Utility.formatTemperature(
                            forecastCursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
                    forecast = String.format("%s - %s - %s/%s",
                            dateString, forecastString, tempHigh, tempLow);

                } else {
                    Log.d(LOG_TAG, "Ops cannot retrieve data from cursor.");
                    return;
                }

                Intent detailsIntent = new Intent(getActivity(), DetailActivity.class);
                detailsIntent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailsIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

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
        mForecastDataAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mForecastDataAdapter.swapCursor(null);
    }
}