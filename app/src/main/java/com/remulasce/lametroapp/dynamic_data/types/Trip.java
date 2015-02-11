package com.remulasce.lametroapp.dynamic_data.types;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remulasce.lametroapp.R;


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
	
	// Context provided for convenience
	public void executeAction(Context context) {
		// ...
	}

    public View getView(ViewGroup parent, Context context, View recycleView) {
        return null;
    }
	
	public float getPriority() {
	    return 50;
	}
	
	public boolean isValid() {
	    return true;
	}

}
