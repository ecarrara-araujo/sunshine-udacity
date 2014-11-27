package eng.ecarrara.sunshine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ecarrara on 27/11/2014.
 */
public class ForecastViewHolder {

    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;

    public ForecastViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.list_item_weather_icon);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
    }

}
