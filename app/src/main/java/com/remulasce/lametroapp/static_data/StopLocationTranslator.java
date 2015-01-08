package com.remulasce.lametroapp.static_data;

import android.location.Location;

import com.remulasce.lametroapp.types.Stop;

/**
 * Created by Remulasce on 1/7/2015.
 */
public interface StopLocationTranslator {
    public BasicLocation getStopLocation(Stop stop);
}
