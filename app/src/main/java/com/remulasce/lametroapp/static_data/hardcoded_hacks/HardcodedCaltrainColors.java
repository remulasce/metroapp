package com.remulasce.lametroapp.static_data.hardcoded_hacks;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Hardcoded Caltrain rail colors
 * Uses colors from caltrain schedule for local, limited, bullet service
 */
public class HardcodedCaltrainColors implements RouteColorer {
    @Override
    public RouteColor getColor(Route r) {

        if (r == null || !r.isValid()) { return null; }

        String raw = r.getString();

        if (r.getAgency().matches("caltrain")) {
            if (raw.equals("LOCAL")) { return new RouteColor("#FFFFFF"); }
            if (raw.equals("LIMITED")) { return new RouteColor("#F7E89D"); }
            if (raw.equals("BABY BULLET")) { return new RouteColor("#F0B2A1"); }
        }

        return null;
    }
}