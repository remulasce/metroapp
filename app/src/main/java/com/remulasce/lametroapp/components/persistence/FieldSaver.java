package com.remulasce.lametroapp.components.persistence;

import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;

import java.util.Collection;

/**
 * Created by Remulasce on 1/11/2015.
 *
 * Interface for handling saving & loading input state
 * eg. ServiceRequests added, current omnibox input, etc.
 */
public interface FieldSaver {
    public void saveServiceRequests(Collection<ServiceRequest> requests);

    /**
     * Retrieves saved service requests more recent than stalenessMillis
     * @param stalenessMillis The maximum time servicerequests will stay cached before being thrown
     *                        out, in ms, or -1 for unlimited cache duration.
     *
     */
    public Collection<ServiceRequest> loadServiceRequests(long stalenessMillis);

    public void saveObject(String key, Object object);
    public Object loadObject(String key);

}
