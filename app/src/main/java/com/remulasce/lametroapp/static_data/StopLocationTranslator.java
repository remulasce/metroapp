package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.types.BasicLocation;
import com.remulasce.lametroapp.types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 */
public interface StopLocationTranslator {
    public BasicLocation getStopLocation(Stop stop);
}
