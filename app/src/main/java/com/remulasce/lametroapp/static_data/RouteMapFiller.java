package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;

import java.util.Collection;

public interface RouteMapFiller {
    Collection<Stop> getNearbyStops(BasicLocation loc);
}
