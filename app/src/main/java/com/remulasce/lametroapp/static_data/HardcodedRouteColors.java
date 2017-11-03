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
    private HardcodedBartColors bartColors = new HardcodedBartColors();
    private HardcodedVTAColors vtaColors = new HardcodedVTAColors();

    @Override
    public RouteColor getColor(Route route) {
        if (route == null || !route.isValid() || route.getAgency() == null) { return null; }

        // Hardcoded. LA-Metro only.
        // Currently backwards-compatible, assumes we're in LA.
        if (route.getAgency().matches("lametro") || route.getAgency().matches("lametro-rail")) {
            return laMetroColors.getColor(route);
        } else if (route.getAgency().matches("caltrain")) {
            return caltrainColors.getColor(route);
        } else if (route.getAgency().matches("BART")) {
            return bartColors.getColor(route);
        } else if (route.getAgency().matches("VTA")) {
            return vtaColors.getColor(route);
        }

        return null;
    }
}

