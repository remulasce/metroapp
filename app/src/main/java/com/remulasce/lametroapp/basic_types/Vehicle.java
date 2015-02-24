package com.remulasce.lametroapp.basic_types;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 8262057532533371341L;


    private String raw = "";
    
    public Vehicle() {}
    public Vehicle( String veh ) {
        raw = veh;
    }
    
    public String getString() {
        return raw;
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
