package com.remulasce.lametroapp.java_core.basic_types;

import java.io.Serializable;

/**
 * Created by Remulasce on 1/7/2015.
 * Seriously, it's latitude and longitude.
 */
public class BasicLocation implements Serializable{
    public final double latitude;
    public final double longitude;

    public BasicLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
