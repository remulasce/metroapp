package com.remulasce.lametroapp.analytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.R;

public class Tracking {

	static Tracker t;
	
	public static Tracker getTracker( Context c ) {
		if (t == null) {
	      GoogleAnalytics analytics = GoogleAnalytics.getInstance( c );
	      
	      t = analytics.newTracker(R.xml.lametro_tracker);
	      
	      t.send(new HitBuilders.EventBuilder()
	    	.setCategory("Analytics")
	    	.setAction("Tracker created")
	    	.setLabel("lametro_tracker")
	    	.build());
		}
		
		return t;
	}
	
	public static long startTime() {
	    return System.currentTimeMillis();
	}
	public static void sendUITime( String name, String label, long startTime ) {
	    sendTime( "UITiming", name, label, startTime );
	}
	public static void sendTime( String category, String name, String label, long startTime ) {
	    long timeSpent = System.currentTimeMillis() - startTime;
	    
	    Log.d(category, name+" "+label+": "+timeSpent);
	    
	    if (t == null) {
	        Log.w(category, "No tracker set, unable to send analytics");
	        return;
	    }
	    
        t.send( new HitBuilders.TimingBuilder()
        .setCategory( category )
        .setValue( timeSpent )
        .setVariable( name )
        .setLabel( label )
        .build() );
	}
	
}
