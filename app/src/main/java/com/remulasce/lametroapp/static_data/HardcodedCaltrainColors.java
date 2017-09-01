package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;

/**
 * Hardcoded rail colors caltrain
 * Uses colors from caltrain schedule for local, limited, bullet service
 */
public class HardcodedCaltrainColors implements RouteColorer {
    @Override
    public RouteColor getColor(Route r) {

        if (r == null || !r.isValid()) { return null; }

        String raw = r.getString();

        // Hardcoded. LA-Metro only.
        // Currently backwards-compatible, assumes we're in LA.
        if (r.getAgency().raw.equals("caltrain")) {
            // Rail lines
            if (raw.equals("LOCAL")) { return new RouteColor("#FFFFFF"); }
            if (raw.equals("LIMITED")) { return new RouteColor("#F7E89D"); }
            if (raw.equals("BABY BULLET")) { return new RouteColor("#F0B2A1"); }
        }

        return null;
    }
}
