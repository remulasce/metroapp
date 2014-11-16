package types;

import java.io.Serializable;

import com.remulasce.lametroapp.LaMetroUtil;

public class Stop implements Serializable {
    private static final long serialVersionUID = 4336735625715608793L;
    
    private String raw;
    
    public Stop() {}
    public Stop(String stopText) {
        raw = stopText;
    }
    public Stop(int stopNum) {
        raw = String.valueOf( stopNum );
    }
    
    public boolean isValid() {
        return LaMetroUtil.isValidStop( raw );
    }
    
    public int getNum() {
        return Integer.valueOf( raw );
    }
    public String getString() {
        return new String(raw);
    }
}
