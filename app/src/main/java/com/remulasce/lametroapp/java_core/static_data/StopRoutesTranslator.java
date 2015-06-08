package com.remulasce.lametroapp.java_core.static_data;

import java.util.Collection;

/**
 * Created by Remulasce on 6/8/2015.
 *
 * Use to get all routes to a specific stop
 */
public interface StopRoutesTranslator {
    public Collection<String> getRoutesToStop(String stopID);
}
