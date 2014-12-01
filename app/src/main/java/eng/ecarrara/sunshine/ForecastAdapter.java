package eng.ecarrara.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ecarrara on 27/11/2014.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
            default:
                layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ForecastViewHolder viewHolder = (ForecastViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int viewType = getItemViewType(cursor.getPosition());
        int weatherId = 0;
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                weatherId = Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_WEATHER_ID));
                break;
            case VIEW_TYPE_FUTURE_DAY:
            default:
                weatherId = Utility.getIconResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_WEATHER_ID));
        }

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(weatherId);

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(mContext, high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }
}
