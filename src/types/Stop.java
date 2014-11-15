package types;

import java.io.Serializable;

public class Stop implements Serializable {
    private static final long serialVersionUID = 4336735625715608793L;
    
    private String raw;
    
    public Stop(String stopText) {
        raw = stopText;
    }
    public Stop(int stopNum) {
        raw = String.valueOf( stopNum );
    }
    
    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }
    
    public int getNum() {
        return Integer.valueOf( raw );
    }
    public String getString() {
        return new String(raw);
    }
}
