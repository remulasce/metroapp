package com.remulasce.lametroapp.static_data;

import java.util.Collection;

/**
 * Created by Remulasce on 12/19/2014.
 */
public interface StopNameTranslator {
    void initialize();
    String getStopName(String stopID);
    Collection<String> getStopID(String stopName);
}
