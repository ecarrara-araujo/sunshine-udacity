package eng.ecarrara.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) != null) {
            // the detail container view will be present only on the larger screen layouts
            // (res/layout-sw600dp. If this view is present, then the activity should be
            // in two panel mode.
            mTwoPane = true;

            // In two pane mode, show de detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new ForecastDetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if(id == R.id.action_preferred_location) {
            Uri geoLocation = (new Uri.Builder())
                    .scheme("geo")
                    .authority("0,0")
                    .appendQueryParameter("q", PreferenceManager.getDefaultSharedPreferences(this)
                            .getString(getString(R.string.pref_location_key),
                                    getString(R.string.pref_location_default)))
                    .build();

            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(geoLocation);
            if(mapIntent.resolveActivity(getPackageManager()) != null){
                startActivity(mapIntent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String date) {
        if(mTwoPane) {
            // In two pane mode, show de detail view i this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction
            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);

            ForecastDetailFragment fragment = new ForecastDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
