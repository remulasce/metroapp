package com.remulasce.lametroapp.java_core;

public class RegionalizationHelper {
    public static final RegionalizationHelper instance = new RegionalizationHelper();
    public String agencyName;
    
    private RegionalizationHelper() {};
    
    public static RegionalizationHelper getInstance()
    {
        return instance;
    }
}