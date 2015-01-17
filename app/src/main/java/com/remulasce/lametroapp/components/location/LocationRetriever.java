package com.remulasce.lametroapp.components.location;

import com.remulasce.lametroapp.basic_types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 *
 * Sort of similar to how Google does it where you don't just request GPS, you request the entire info.
 *
 * But, here we can do predictions based on what train you're on or something.
 */
public interface LocationRetriever {
    public double getCurrentDistanceToStop(Stop stop);
}
