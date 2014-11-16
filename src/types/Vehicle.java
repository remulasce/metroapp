package types;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 8262057532533371341L;


    String raw = "";
    
    public Vehicle() {}
    public Vehicle( String veh ) {
        raw = veh;
    }
    
    public String getString() {
        return new String(raw);
    }
    
    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }
}
