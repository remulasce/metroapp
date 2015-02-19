package com.remulasce.lametroapp.static_data;

import com.remulasce.lametroapp.basic_types.BasicLocation;
import com.remulasce.lametroapp.basic_types.Stop;

public interface StopLocationTranslator {
    void initialize();

    public BasicLocation getStopLocation(Stop stop);
}
