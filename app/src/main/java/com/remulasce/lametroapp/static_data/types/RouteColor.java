package com.remulasce.lametroapp.static_data.types;

import java.io.Serializable;

/**
 * Created by Remulasce on 2/25/2015.
 *
 * Use Color.parseColor to actually use this.
 * We don't use the values internally, so keeping it as a string (which is how it comes from Metro,
 * and how Color.parseColor expects it) makes some sense.
 */
public class RouteColor implements Serializable {
    public RouteColor(String color) {
        this.color = color;
    }

    public String color;
}
