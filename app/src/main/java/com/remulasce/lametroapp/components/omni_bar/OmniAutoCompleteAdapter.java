package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;
import com.remulasce.lametroapp.static_data.AutoCompleteCombinedFiller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Adapter for the autocomplete drop-down-list on stop entri field. */
public class OmniAutoCompleteAdapter extends ArrayAdapter implements Filterable {
  private final String TAG = "OmniAutoCompleteAdapter";

  private ArrayList<OmniAutoCompleteEntry> resultList = new ArrayList<OmniAutoCompleteEntry>();
  private final UserStateProvider userStateProvider;
  private final AutoCompleteCombinedFiller autoCompleteCombinedFiller;
  private final LocationRetriever locations;
  private final RouteColorer colors;

  private FilterTaskCompleteListener completeListener;
  private final AutoCompleteFiller autoCompleteFiller;

  // Colors optional.
  public OmniAutoCompleteAdapter(
      Context context,
      UserStateProvider userStateProvider,
      int resource,
      int textView,
      AutoCompleteCombinedFiller t,
      LocationRetriever locations,
      RouteColorer colors) {
    super(context, resource, textView);
    autoCompleteCombinedFiller = t;
    this.userStateProvider = userStateProvider;
    this.locations = locations;
    this.colors = colors;

    autoCompleteFiller =
        new MetroAutoCompleteFiller(
            autoCompleteCombinedFiller,
            autoCompleteCombinedFiller,
            autoCompleteCombinedFiller,
            userStateProvider,
            locations);
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
    Filter filter =
        new Filter() {
          @Override
          protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null) {

              Collection<OmniAutoCompleteEntry> results =
                  autoCompleteFiller.getAutoCompleteEntries(constraint.toString());

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
                ArrayList<OmniAutoCompleteEntry> n =
                    new ArrayList<OmniAutoCompleteEntry>(
                        (Collection<OmniAutoCompleteEntry>) results.values);
                Collections.sort(
                    n,
                    new Comparator<OmniAutoCompleteEntry>() {
                      @Override
                      public int compare(
                          OmniAutoCompleteEntry omniAutoCompleteEntry,
                          OmniAutoCompleteEntry omniAutoCompleteEntry2) {
                        if (omniAutoCompleteEntry.getPriority()
                            < omniAutoCompleteEntry2.getPriority()) {
                          return 1;
                        } else if (omniAutoCompleteEntry.getPriority()
                            > omniAutoCompleteEntry2.getPriority()) {
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

    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    LinearLayout view;

    if (recycleView != null && recycleView.getId() == R.id.omnibar_dropdown_item) {
      view = (LinearLayout) recycleView;
    } else {
      view = (LinearLayout) inflater.inflate(R.layout.omnibar_dropdown_item, parent, false);
    }

    OmniAutoCompleteEntry omniAutoCompleteEntry = resultList.get(position);

    // Set the main station name
    TextView name = (TextView) view.findViewById(R.id.omnibar_item_station_name);
    name.setText(omniAutoCompleteEntry.toString());

    // Set the color bars to the left of the name to show what routes serve the station
    ViewGroup colorsLayout = (ViewGroup) view.findViewById(R.id.autocomplete_item_color_layout);

    RouteColorerTask routeColorerTask =
        new RouteColorerTask(colorsLayout, autoCompleteCombinedFiller, colors);
    // Setting the task helps with concurrency during recycling
    // Prevents previous tasks from completing when the layout gets recycled to a new stop

    Object tag = colorsLayout.getTag();
    if (tag instanceof RouteColorerTask) {
      ((RouteColorerTask) tag).cancel(true);
    }
    colorsLayout.setTag(routeColorerTask);
    routeColorerTask.execute(omniAutoCompleteEntry.getStops());

    TextView distance = (TextView) view.findViewById(R.id.omnibar_item_distance_text);
    distance.setText(
        LaMetroUtil.convertMetersToDistanceDisplay(
            locations.getCurrentDistanceToStop(omniAutoCompleteEntry.getStops().get(0))));

    return view;
  }

  private List<RouteColor> getColorBars(OmniAutoCompleteEntry entry) {
    ArrayList<RouteColor> colorBars = new ArrayList<RouteColor>();

    Collection<Stop> stops = entry.getStops();

    for (Stop stop : stops) {
      Collection<Route> routes = autoCompleteCombinedFiller.getRoutesToStop(stop);

      for (Route r : routes) {
        RouteColor color = colors.getColor(r);
        if (color != null
            && color.color != null
            && !color.color.isEmpty()
            && !colorBars.contains(color)) {
          colorBars.add(color);
        }
      }
    }

    Log.v(TAG, "Got colors for " + entry + ", " + colorBars);

    return colorBars;
  }
}
