package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.util.ArrayList;
import java.util.Collection;

public class RegionalizationHelper {
    public static final RegionalizationHelper instance = new RegionalizationHelper();

    // All agencies that can be enabled (have input sql dbs)
    private Collection<Agency> installedAgencies = new ArrayList<Agency>();

    // All agencies that should be queried on input
    private Collection<Agency> activeAgencies = new ArrayList<Agency>();

    private RegionalizationHelper() {};
    
    public static RegionalizationHelper getInstance()
    {
        return instance;
    }


    public Collection<Agency> getInstalledAgencies() { return installedAgencies; }
    public void setInstalledAgencies(Collection<Agency> agencies) { this.installedAgencies = agencies; }

    public Collection<Agency> getActiveAgencies() {
        return activeAgencies;
    }
    public void setActiveAgencies(Collection<Agency> agencies) {
        this.activeAgencies = agencies;
    }
}