package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class RegionalizationHelper {
    private static final RegionalizationHelper instance = new RegionalizationHelper();
    public static final String ACTIVE_REGIONS = "active_regions";
    private static FieldSaver persistence;

    public static void setPersistence(FieldSaver persistence) {
        RegionalizationHelper.persistence = persistence;
    }

    // All agencies that can be enabled (have input sql dbs)
    private Collection<Agency> installedAgencies = new ArrayList<Agency>();

    // All agencies that should be queried on input
    private Collection<Agency> activeAgencies = new ArrayList<Agency>();

    private RegionalizationHelper() {};
    public void loadPersistedAgencies() {
        if (persistence != null) {
            Object persistedDeal = persistence.loadObject(ACTIVE_REGIONS);

            if (persistedDeal != null && persistedDeal instanceof Collection) {
                try {
                    activeAgencies = (Collection<Agency>) persistedDeal;
                } catch (Exception exception) {
                    Log.w("RegionalizationHelper", "Error loading persisted file, dropping it" );
                    persistence.saveObject(ACTIVE_REGIONS, null);
                }
            }
        }
    }
    
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

        if (persistence != null) {
            persistence.saveObject("active_regions", activeAgencies);
        }
    }
}