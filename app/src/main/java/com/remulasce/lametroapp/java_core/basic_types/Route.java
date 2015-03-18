package com.remulasce.lametroapp.java_core.basic_types;

import com.remulasce.lametroapp.static_data.types.RouteColor;

import java.io.Serializable;

public class Route implements Serializable {
    private static final long serialVersionUID = -1330979643298664422L;

    // 802, 754, etc.
    private String raw = "";

    // Optional. Check null before using.
    private RouteColor color;

    public Route() {}
    public Route( String route ) {
        raw = route;
    }
    public Route( String route, RouteColor color) {
        raw = route; this.color = color;
    }

    public String getString() {
        return raw;
    }

    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }

    public void setColor(RouteColor color) {
        this.color = color;
    }

    public RouteColor getColor() { return color; }

    // Ugh.
    public int hashCode() {
        return raw.hashCode();
    }
    public boolean equals( Object o ) {
        if ( o.getClass() != this.getClass()) { return false; }

        return (o.hashCode() == this.hashCode());
    }
}
