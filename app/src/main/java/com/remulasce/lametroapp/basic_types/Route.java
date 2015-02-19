package com.remulasce.lametroapp.basic_types;

import java.io.Serializable;

public class Route implements Serializable {
    private static final long serialVersionUID = -1330979643298664422L;

    private String raw = "";

    public Route() {}
    public Route( String route ) {
        raw = route;
    }

    public String getString() {
        return new String( raw );
    }

    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }

    // Ugh.
    public int hashCode() {
        return raw.hashCode();
    }
    public boolean equals( Object o ) {
        if ( o.getClass() != this.getClass()) { return false; }

        return (o.hashCode() == this.hashCode());
    }
}
