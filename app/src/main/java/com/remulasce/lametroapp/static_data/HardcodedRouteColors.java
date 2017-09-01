package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Colors for lines based on assumed agency + color combinations.
 */
public class HardcodedRouteColors implements RouteColorer {

    private HardcodedLaMetroColors laMetroColors = new HardcodedLaMetroColors();
    private HardcodedCaltrainColors caltrainColors = new HardcodedCaltrainColors();

    @Override
    public RouteColor getColor(Route route) {
        if (route == null || !route.isValid() || route.getAgency() == null) { return null; }

        String raw = route.getString();

        // Hardcoded. LA-Metro only.
        // Currently backwards-compatible, assumes we're in LA.
        if (route.getAgency().raw.equals("lametro") || route.getAgency().raw.equals("lametro-rail")) {
            return laMetroColors.getColor(route);
        } else if (route.getAgency().raw.equals("caltrain")) {
            return caltrainColors.getColor(route);
        }

        return null;
    }
}

