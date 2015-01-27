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
          t.enableAdvertisingIdCollection(true);
		}
		
		return t;
	}
	
	public static long startTime() {
	    return System.currentTimeMillis();
	}
    // Avg. for like frame updates that are too numerous to send directly.
    public static void averageUITime( String name, String label, long startTime ) {

    }
    // sendUITime for stuff that happens as direct user input, and should be individually tracked.
	public static void sendUITime( String name, String label, long startTime ) {
	    sendTime( "UITiming", name, label, startTime );
	}
    public static void sendRawUITime( String name, String label, long timeSpent ) {
        sendRawTime( "UITiming", name, label, timeSpent );
    }
    public static long timeSpent(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
    public static void sendTime( String category, String name, String label, long startTime) {
        sendRawTime( category, name, label, timeSpent( startTime ) );
    }
	public static void sendRawTime( String category, String name, String label, long timeSpent ) {
	    Log.v(category, name+" "+label+": "+timeSpent);

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
