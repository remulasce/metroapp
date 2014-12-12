package com.remulasce.lametroapp.types;

import java.io.Serializable;

public class Destination implements Serializable {
    private static final long serialVersionUID = 6069634261484767760L;

    String raw = "";
    
    public Destination() {}
    public Destination( String dest) {
        raw = dest;
    }
    
    public String getString() {
        return new String(raw);
    }
    
    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }
}
