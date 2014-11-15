package types;

import java.io.Serializable;

import com.remulasce.lametroapp.LaMetroUtil;

public class Route implements Serializable {
    private static final long serialVersionUID = -1330979643298664422L;
    
    String raw;
	
	public Route( String route ) {
	    raw = route;
	}
	
	public String getString() {
	    return new String(raw);
	}
	
	public boolean isValid() {
	    return raw != null && !raw.isEmpty() && LaMetroUtil.isValidRoute( raw );
	}
}
