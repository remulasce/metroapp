package com.remulasce.lametroapp.components;

import android.app.Service;

import com.remulasce.lametroapp.types.ServiceRequest;

import java.util.Collection;

/**
 * Created by Remulasce on 1/11/2015.
 *
 * Interface for handling saving & loading input state
 * eg. ServiceRequests added, current omnibox input, etc.
 */
public interface FieldSaver {
    public void saveServiceRequests(Collection<ServiceRequest> requests);
    public Collection<ServiceRequest> loadServiceRequests();
}
