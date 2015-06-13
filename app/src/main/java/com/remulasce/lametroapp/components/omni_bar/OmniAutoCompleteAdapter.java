package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter for the autocomplete drop-down-list on stop entri field.
 */
public class OmniAutoCompleteAdapter extends ArrayAdapter implements Filterable
{
    private final String TAG = "OmniAutoCompleteAdapter";

    private ArrayList<OmniAutoCompleteEntry> resultList = new ArrayList<OmniAutoCompleteEntry>();
    private final AutoCompleteCombinedFiller autoCompleteCombinedFiller;
    private final LocationRetriever locations;
    private final RouteColorer colors;

    private FilterTaskCompleteListener completeListener;
    private final AutoCompleteFiller autoCompleteFiller;

    // Colors optional.
    public OmniAutoCompleteAdapter(Context context, int resource, int textView, AutoCompleteCombinedFiller t,
                                   LocationRetriever locations, RouteColorer colors) {
        super(context, resource, textView);
        autoCompleteCombinedFiller = t;
        this.locations = locations;
        this.colors = colors;

        autoCompleteFiller = new MetroAutoCompleteFiller(autoCompleteCombinedFiller, autoCompleteCombinedFiller, locations);
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public OmniAutoCompleteEntry getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    Collection<OmniAutoCompleteEntry> results = autoCompleteFiller.getAutoCompleteEntries(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    try {
                        ArrayList<OmniAutoCompleteEntry> n = new ArrayList<OmniAutoCompleteEntry>((Collection<OmniAutoCompleteEntry>) results.values);
                        Collections.sort(n, new Comparator<OmniAutoCompleteEntry>() {
                            @Override
                            public int compare(OmniAutoCompleteEntry omniAutoCompleteEntry, OmniAutoCompleteEntry omniAutoCompleteEntry2) {
                                if (omniAutoCompleteEntry.getPriority() < omniAutoCompleteEntry2.getPriority()) {
                                    return 1;
                                } else if (omniAutoCompleteEntry.getPriority() > omniAutoCompleteEntry2.getPriority()) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        resultList = n;
                        notifyDataSetChanged();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Results of omniautocomplete publish results not expected");
                    }
                } else {
                    notifyDataSetInvalidated();
                }
                if (completeListener != null) {
                    if (constraint != null) {
                        completeListener.filterCompletionDetails(constraint.toString());
                    } else {
                        completeListener.filterCompletionDetails(null);
                    }
                }
            }
        };
        return filter;
    }

    public void setCompleteListener(FilterTaskCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    @Override
    public View getView(int position, View recycleView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout view;

        if (recycleView != null && recycleView.getId() == R.id.omnibar_dropdown_item) {
            view = (LinearLayout)recycleView;
        } else {
            view = (LinearLayout) inflater.inflate(R.layout.omnibar_dropdown_item, parent, false);
        }

        OmniAutoCompleteEntry omniAutoCompleteEntry = resultList.get(position);

        // Set the main station name
        TextView name = (TextView) view.findViewById(R.id.omnibar_item_station_name);
        name.setText(omniAutoCompleteEntry.toString());


        // Set the color bars to the left of the name to show what routes serve the station
        // TODO: This is FS access on UI thread. Very slow and bad.

//        List<RouteColor> colorBars = getColorBars(omniAutoCompleteEntry);
        ViewGroup colorsLayout = (ViewGroup) view.findViewById(R.id.autocomplete_item_color_layout);

        /*
        List<View> updateViews = new ArrayList<View>();
        // If we change the size of the view, we should invalidate it and redraw.
        boolean sizeChanged = false;
        // Find all the color bar views we can reuse
        for (int i = 0; i < colorsLayout.getChildCount(); i++) {
            View v = colorsLayout.getChildAt(i);

            Object tag = v.getTag();
            if (tag instanceof RouteColor) {
                updateViews.add(v);
            }
        }

        // Get all the Arrivals displayed
        for (RouteColor c : colorBars) {
            View updateColorView;

            // If there's recycled views to use
            if (updateViews.size() > 0) {
                updateColorView = updateViews.get(0);
                updateViews.remove(0);
            }
            // If there's no recycled views left, make one.
            else {
                sizeChanged = true;
                updateColorView = inflater.inflate(R.layout.omnibar_dropdown_color_bar, colorsLayout, false);
                updateColorView.setTag(c);

                colorsLayout.addView(updateColorView);
            }

            updateColorView.setBackgroundColor(Color.parseColor(c.color));
        }

        // Remove extra recycled arrivals
        for (View v : updateViews) {
            sizeChanged = true;
            colorsLayout.removeView(v);
        }

        // This might not actually be necessary.
        if (sizeChanged) {
            colorsLayout.requestLayout();
            colorsLayout.invalidate();
        }
        */

        RouteColorerTask routeColorerTask = new RouteColorerTask(colorsLayout, autoCompleteCombinedFiller, colors);
        // Setting the task helps with concurrency during recycling
        // Prevents previous tasks from completing when the layout gets recycled to a new stop
        colorsLayout.setTag(routeColorerTask);
        routeColorerTask.execute(omniAutoCompleteEntry.getStops());

        return view;
    }

    private List<RouteColor> getColorBars(OmniAutoCompleteEntry entry) {
        ArrayList<RouteColor> colorBars = new ArrayList<RouteColor>();

        Collection<Stop> stops = entry.getStops();

        for (Stop stop : stops) {
            Collection<Route> routes = autoCompleteCombinedFiller.getRoutesToStop(stop);

            for (Route r : routes) {
                RouteColor color = colors.getColor(r);
                if (color != null && color.color != null && !color.color.isEmpty() && !colorBars.contains(color)) {
                    colorBars.add(color);
                }
            }
        }

        Log.v(TAG, "Got colors for "+entry+", "+colorBars);

        return colorBars;
    }
}
