package com.remulasce.lametroapp.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.R;

public class Tracking {

	static Tracker t;
	
	public static Tracker getTracker( Context c ) {
		if (t == null) {
	      GoogleAnalytics analytics = GoogleAnalytics.getInstance( c );
	      
	      //Test property ID
	      t = analytics.newTracker(R.xml.lametro_tracker);
		}
		
		return t;
	}
	
}
