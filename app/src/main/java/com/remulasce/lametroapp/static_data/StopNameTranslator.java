package com.remulasce.lametroapp.static_data;

/**
 * Created by Remulasce on 12/19/2014.
 */
public interface StopNameTranslator {
    void initialize();
    String getStopName(String stopID);
    String getStopID(String stopName);
}
