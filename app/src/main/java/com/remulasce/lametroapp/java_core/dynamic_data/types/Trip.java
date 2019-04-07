package com.remulasce.lametroapp.java_core.dynamic_data.types;

import java.io.Serializable;


/** Individual trips are displayed to the user as the result of his
 * input.
 * 
 * Each trip provides information, and can provide additional response
 * to being 'taken'
 * 
 */
public class Trip implements Serializable {
	private String text = "...";
	
	public String getInfo() {
		return toString();
	}
	
	public void setText( String text ) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}

	public float getPriority() {
	    return 50;
	}
	
	public boolean isValid() {
	    return true;
	}

    public void dismiss() { }
}
