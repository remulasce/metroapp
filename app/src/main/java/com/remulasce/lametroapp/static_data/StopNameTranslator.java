package com.remulasce.lametroapp.static_data;

import java.util.Collection;

public interface StopNameTranslator {
    String getStopName(String stopID);
    Collection<String> getStopID(String stopName);
}
