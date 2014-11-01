package com.remulasce.lametroapp.pred;


/** Individual trips are displayed to the user as the result of his
 * input.
 * 
 * Each trip provides information, and can provide additional response
 * to being 'taken'
 * 
 * @author Fintan
 *
 */
public class Trip {
	protected String text = "...";
	
	public String getInfo() {
		return toString();
	}
	
	public void setText( String text ) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
	
	// public void takeTrip
}
