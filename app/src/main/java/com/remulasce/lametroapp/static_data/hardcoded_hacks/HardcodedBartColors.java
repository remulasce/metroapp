package com.remulasce.lametroapp.static_data.hardcoded_hacks;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Hardcoded Bart colors
 * Uses colors from Bart api information to map from Bart routes to more familiar lines
 */
public class HardcodedBartColors implements RouteColorer {
    @Override
    public RouteColor getColor(Route r) {

        if (r == null || !r.isValid()) { return null; }

        // Bug at some higher level results in routes like " ROUTE 11"
        // TODO: clean this at the scraper level
        String raw = r.getString().trim();

        if (r.getAgency().matches("BART")) {
            // The Bart arrivals api does not use the same route designations as the gtfs tables.
            // We must use the colors returned alongside the predictions, then.
            if (r.getColor() != null) {
                return r.getColor();
            }

            // Otherwise check for known routes from the scraper.
            if (raw.equals("ROUTE 1")) { return new RouteColor("#FFFF33"); }
            if (raw.equals("ROUTE 2")) { return new RouteColor("#FFFF33"); }
            if (raw.equals("ROUTE 3")) { return new RouteColor("#FF9933"); }
            if (raw.equals("ROUTE 4")) { return new RouteColor("#FF9933"); }
            if (raw.equals("ROUTE 5")) { return new RouteColor("#339933"); }
            if (raw.equals("ROUTE 6")) { return new RouteColor("#339933"); }
            if (raw.equals("ROUTE 7")) { return new RouteColor("#FF0000"); }
            if (raw.equals("ROUTE 8")) { return new RouteColor("#FF0000"); }
            if (raw.equals("ROUTE 11")) { return new RouteColor("#0099CC"); }
            if (raw.equals("ROUTE 12")) { return new RouteColor("#0099CC"); }
            if (raw.equals("ROUTE 19")) { return new RouteColor("#D5CFA3"); }
            if (raw.equals("ROUTE 20")) { return new RouteColor("#D5CFA3"); }
        }

        return null;
    }
}
