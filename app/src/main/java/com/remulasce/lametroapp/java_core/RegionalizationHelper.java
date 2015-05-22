package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.util.Collection;

public class RegionalizationHelper {
    public static final RegionalizationHelper instance = new RegionalizationHelper();

    // Old, direct name.
    // Should still refer to the overall region- lametro, etc, instead of the nextrip agency.
    public String agencyName;

    // New way
    // Supports N agencies at any location
    // So we should make a general "is agency useful here" method, instead of defining
    // regions.
    private Collection<Agency> agencies;

    private RegionalizationHelper() {};
    
    public static RegionalizationHelper getInstance()
    {
        return instance;
    }

    public Collection<Agency> getActiveAgencies() {
        return agencies;
    }
    public void setActiveAgencies(Collection<Agency> agencies) {
        this.agencies = agencies;
    }
}