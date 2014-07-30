package eng.ecarrara.sunshine;

/**
 * Created by ecarrara on 7/30/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment containing the weather forecast view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final String[] mockWeatherData = {
                "Today - Sunny  - 88/63",
                "Tomorrow - Cloudy - 70/90",
                "Weds - Rainy - 35/97",
                "Thurs - Stormy - 89/127",
                "Fri - Sunny - 98/67",
                "Sat - Cloudy - 124/98"
        };

        List<String> weatherData = new ArrayList<String>(Arrays.asList(mockWeatherData));

        ArrayAdapter<String> weatherDataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weatherData);

        ListView weatherListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        weatherListView.setAdapter(weatherDataAdapter);

        return rootView;
    }
}