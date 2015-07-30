package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.util.Collection;

public class RegionalizationHelper {
    public static final RegionalizationHelper instance = new RegionalizationHelper();

    // TODO: Separate list of all installed agencies
    // TODO  And, code to set which ones are active or not.

    // Supports N agencies at any location
    // So we should make a general "is agency useful here" method, instead of defining
    // regions.
    private Collection<Agency> activeAgencies;

    private RegionalizationHelper() {};
    
    public static RegionalizationHelper getInstance()
    {
        return instance;
    }

    public Collection<Agency> getActiveAgencies() {
        return activeAgencies;
    }
    public void setActiveAgencies(Collection<Agency> agencies) {
        this.activeAgencies = agencies;
    }
}