package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Created by Remulasce on 2/25/2015.
 */
public class HardcodedMetroColors implements RouteColorer {
    @Override
    public RouteColor getColor(Route r) {
        if (r == null || !r.isValid()) { return null; }

        String raw = r.getString();

        // Rail lines
        if (raw.equals("801")) { return new RouteColor("#004DAC"); }
        if (raw.equals("802")) { return new RouteColor("#EE3A43"); }
        if (raw.equals("803")) { return new RouteColor("#2EAB00"); }
        if (raw.equals("804")) { return new RouteColor("#DA7C20"); }
        if (raw.equals("805")) { return new RouteColor("#9561A9"); }
        if (raw.equals("806")) { return new RouteColor("#0177A5"); }
        // BRT routes
        if (raw.equals("901")) { return new RouteColor("#FF5A00"); }
        if (raw.equals("910")) { return new RouteColor("#656D74"); }
        // Rapid routes
        if (raw.matches("^7\\d\\d$")) { return new RouteColor("#000000"); }

        return null;
    }
}
