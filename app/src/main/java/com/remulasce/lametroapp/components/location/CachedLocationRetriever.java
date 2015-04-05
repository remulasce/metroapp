package com.remulasce.lametroapp.components.location;

import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;

/**
 * Created by Remulasce on 4/4/2015.
 *
 * CachedLocationRetriever solves the problem of Trips using different current locations for
 *   their priorities, due to them checking in for updated locations indepentently.
 *
 * This was due to performance concerns of the previous location retriever, which would
 *   lag the system if every trip checked in at once every refresh.
 *
 * Here we cache distances to various locations, allowing trips to checkin from us every update.
 *
 * When we receive new locations from the system, we will update all of the cached distances ourselves.
 * That makes actual access faster and also ensures every distance is based off proximity to the
 *   same location.
 *
 * In-between app runs we persist our most recent location. This will probably be wrong between loads,
 *   but makes it more predictable what will appear first on load.
 *
 */
public class CachedLocationRetriever implements LocationRetriever {
    @Override
    public double getCurrentDistanceToStop(Stop stop) {
        return 0;
    }
}
