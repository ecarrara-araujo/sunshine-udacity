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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;

import eng.ecarrara.sunshine.data.WeatherContract;
import eng.ecarrara.sunshine.data.WeatherContract.LocationEntry;
import eng.ecarrara.sunshine.data.WeatherContract.WeatherEntry;
import eng.ecarrara.sunshine.sync.SunshineSyncAdapter;

/**
 * A fragment containing the weather forecast view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }

    private ForecastAdapter mForecastDataAdapter;
    private boolean mUseTodayLayout;

    private static final int FORECAST_LOADER = 0;
    private static final String LIST_POSITION_KEY = "list_position";
    private String mLocation;
    private int mPosition;
    private ListView mForecastListView;

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
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LOCATION_SETTING,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_LONG_DESC = 5;
    public static final int COL_WEATHER_WEATHER_ID = 6;
    public static final int COL_LOCATION_SETTING = 7;
    public static final int COL_COORD_LAT = 8;
    public static final int COL_COORD_LONG = 9;


    public ForecastFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(LIST_POSITION_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
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
        } else if(id == R.id.action_preferred_location) {
           openPreferredLocationInMap();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mForecastDataAdapter = new ForecastAdapter(getActivity(), null, 0);
        mForecastDataAdapter.setUseTodayLayout(mUseTodayLayout);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mForecastListView.setAdapter(mForecastDataAdapter);
        mForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mForecastDataAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(COL_WEATHER_DATE));
                }
                mPosition = position;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(LIST_POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(LIST_POSITION_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
//        Intent sunshineService  = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        sunshineService.putExtra(SunshineService.LOCATION_QUERY_EXTRA,
//                Utility.getPreferredLocation(getActivity()));
//
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, sunshineService,
//                PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pi);
//
//        getActivity().startService(sunshineService);
        SunshineSyncAdapter.syncImmediately(getActivity());
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
        if(mPosition != ListView.INVALID_POSITION) {
            mForecastListView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mForecastDataAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if(mForecastDataAdapter != null) {
            mForecastDataAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    private void openPreferredLocationInMap() {
        if(null != mForecastDataAdapter) {
            Cursor c = mForecastDataAdapter.getCursor();
            if(null != c) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);

                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(geoLocation);
                if(mapIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(mapIntent);
                }
            }
        }
    }
}