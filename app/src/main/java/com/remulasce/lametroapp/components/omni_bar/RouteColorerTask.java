package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.StopRoutesTranslator;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Remulasce on 6/12/2015.
 *
 * Asynctask searches SQL for routes to a stop, then fills in the colorbars for a listitem.
 *
 * Made for the autocomplete dropdown box. Supports multiple colors per stop, so make sure
 * the viewgroup its handed can expand safely.
 */
public class RouteColorerTask extends AsyncTask<Collection<Stop>, Void, Collection<RouteColor>>{
    private String url;
    private final WeakReference<ViewGroup> viewGroupWeakReference;
    private final WeakReference<StopRoutesTranslator> stopRoutesTranslatorWeakReference;
    private final WeakReference<RouteColorer> routeColorerWeakReference;

    public RouteColorerTask(ViewGroup colorBarLayout, StopRoutesTranslator stopRoutes, RouteColorer routeColorer) {
        viewGroupWeakReference = new WeakReference<ViewGroup>(colorBarLayout);
        stopRoutesTranslatorWeakReference = new WeakReference<StopRoutesTranslator>(stopRoutes);
        routeColorerWeakReference = new WeakReference<RouteColorer>(routeColorer);

        // For recycled views, there may already be color bars created.
        // We want to reuse the Views, but don't want to see the old colors.
        // So we set them to INVISIBLE until we finish loading.
        // Also set the 'loading' state.
        clearExistingColors(colorBarLayout);
    }

    private void clearExistingColors(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);

            Object tag = v.getTag();
            if (tag instanceof RouteColor) {
                v.setVisibility(View.INVISIBLE);
            }
        }

        layout.setBackgroundColor(Color.WHITE);
    }

    // Actual download method, run in the task thread
    @Override
    protected Collection<RouteColor> doInBackground(Collection<Stop>... params) {
        return getColorBars(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Collection<RouteColor> colors) {
        if (isCancelled()) {
            return;
        }

        if (viewGroupWeakReference != null) {
            ViewGroup colorsLayout = viewGroupWeakReference.get();
            // getTag == this ensures that the view has not been recycled and had a new
            // color lookup task set to it.
            if (colorsLayout != null && colorsLayout.getTag() == this) {
                fillinColorBars(colorsLayout, colors);
                colorsLayout.setBackgroundColor(Color.parseColor("#ffdddddd"));
            }

        }
    }

    private List<RouteColor> getColorBars(Collection<Stop> stops) {
        ArrayList<RouteColor> colorBars = new ArrayList<RouteColor>();

        for (Stop stop : stops) {
            Collection<Route> routes = stopRoutesTranslatorWeakReference.get().getRoutesToStop(stop);

            for (Route r : routes) {
                RouteColor color = routeColorerWeakReference.get().getColor(r);
                if (color != null && color.color != null && !color.color.isEmpty() && !colorBars.contains(color)) {
                    colorBars.add(color);
                }
            }
        }

        return colorBars;
    }

    private void fillinColorBars(ViewGroup colorsLayout, Collection<RouteColor> colorBars) {
        LayoutInflater inflater = (LayoutInflater) colorsLayout.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


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

            // Recycled views were invisible because they previously had the wrong colors.
            updateColorView.setVisibility(View.VISIBLE);
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
    }
}
