package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.location.GlobalLocationProvider;
import com.remulasce.lametroapp.java_core.location.LocationRetriever;

import java.util.ArrayList;
import java.util.Collection;

public class RegionalizationHelper {
  private static final RegionalizationHelper instance = new RegionalizationHelper();
  public static final String ACTIVE_REGIONS = "active_regions";
  public static final String AUTODETECT_REGIONS = "autodetect_regions";
  private static FieldSaver persistence;
  private static final String TAG = "RegionalizationHelper";

  public static void setPersistence(FieldSaver persistence) {
    RegionalizationHelper.persistence = persistence;
  }

  // All agencies that can be enabled (have input sql dbs)
  private Collection<Agency> installedAgencies = new ArrayList<Agency>();

  // All agencies that should be queried on input
  private Collection<Agency> activeAgencies = new ArrayList<Agency>();

  // Help us figure out when to update the active agencies. Should be like, barely ever.
  // Do we even want to support hot-swapping?
  private long lastRegionUpdateTime = 0;
  private boolean autoDetect = true;

  private RegionalizationHelper() {}
  ;

  public void loadPersistedAgencies() {
    if (installedAgencies == null || installedAgencies.size() == 0) {
      Log.w(
          TAG,
          "No installed agencies, fail-fast instead of trying to load persisted active regions.");
      activeAgencies = new ArrayList<Agency>();
      return;
    }
    // If there's nothing saved, default to autodetect
    if (persistence == null) {
      Log.w(
          TAG,
          "RegionalizationHelper tried to load persisted agencies, but there's no saver set. Defaulting to auto-detect.");
      autodetectRegions();
    } else {
      // We can try to load from persistence
      Object useAutoDetect = persistence.loadObject(AUTODETECT_REGIONS);
      if (useAutoDetect != null && useAutoDetect instanceof Boolean) {
        try {
          autoDetect = (Boolean) useAutoDetect;
        } catch (Exception e) {
          Log.w(TAG, "Error loading persisted file, dropping it");
          persistence.saveObject(AUTODETECT_REGIONS, null);
        }
      }

      Object persistedDeal = persistence.loadObject(ACTIVE_REGIONS);
      if (persistedDeal != null && persistedDeal instanceof Collection) {
        try {
          activeAgencies = (Collection<Agency>) persistedDeal;
        } catch (Exception exception) {
          Log.w(TAG, "Error loading persisted file, dropping it");
          persistence.saveObject(ACTIVE_REGIONS, null);
        }
      } else { // No or invalid persisted active agencies. Default to autodetected regions.
        autodetectRegions();
      }
    }
  }

  public static RegionalizationHelper getInstance() {
    return instance;
  }

  public void setAutoDetect(boolean autoDetect) {
    this.autoDetect = autoDetect;

    if (persistence != null) {
      persistence.saveObject(AUTODETECT_REGIONS, autoDetect);
    }

    if (autoDetect) {
      autodetectRegions();
    }
  }

  public boolean getAutoDetect() {
    return this.autoDetect;
  }

  public Collection<Agency> getInstalledAgencies() {
    return installedAgencies;
  }

  public void setInstalledAgencies(Collection<Agency> agencies) {
    this.installedAgencies = agencies;
  }

  public Collection<Agency> getActiveAgencies() {
    if (autoDetect && System.currentTimeMillis() > lastRegionUpdateTime + 60000) {
      autodetectRegions();
    }

    return activeAgencies;
  }

  private void autodetectRegions() {
    LocationRetriever retriever = GlobalLocationProvider.getRetriever();
    if (retriever == null) {
      Log.w(TAG, "Couldn't automatically update current region from helper");
      lastRegionUpdateTime = System.currentTimeMillis() - 55000;
      activeAgencies = getDefaultRegions();
      return;
    }
    BasicLocation current = retriever.getCurrentLocation();
    if (current == null) {
      Log.w(TAG, "Couldn't automatically update current region from helper");
      lastRegionUpdateTime = System.currentTimeMillis() - 55000;
      activeAgencies = getDefaultRegions();
    } else {
      lastRegionUpdateTime = System.currentTimeMillis();
      Collection<Agency> newActiveAgencies = new ArrayList<Agency>();
      for (Agency a : installedAgencies) {
        if (a.hasBounds()) {
          if (current.latitude > a.minLatLonBound.latitude
              && current.latitude < a.maxLatLonBound.latitude
              && current.longitude > a.minLatLonBound.longitude
              && current.longitude < a.maxLatLonBound.longitude) {
            newActiveAgencies.add(a);
          } else {
            // This is where agencies which have bounds, but aren't in them, are not added.
            // As you can see, it's empty.
          }
        } else {
          Log.w(TAG, "RegionalizationHelper can't regionalize an agency with no bounds: " + a);
        }
      }

      setActiveAgencies(newActiveAgencies);
    }

    Log.d(TAG, "RegionalizationHelper pulled current location: " + current);
  }

  /** Because Android location thing doesn't work so much any more... Default to all agencies */
  private ArrayList<Agency> getDefaultRegions() {
    return new ArrayList<Agency>(installedAgencies);
  }

  public void setActiveAgencies(Collection<Agency> agencies) {
    this.activeAgencies = agencies;

    if (persistence != null) {
      persistence.saveObject("active_regions", activeAgencies);
    }
  }
}
