package com.remulasce.lametroapp.analytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.R;

import java.util.HashMap;

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

    // in ms from nanotime/1000
	public static long startTime() {
	    return System.nanoTime() / 1000;
	}


    static class AveragedDatum {
        public double totalValue = 0;
        public double numPoints = 0;
    }

    static HashMap<String, HashMap<String, AveragedDatum>> averagedValues = new HashMap<String, HashMap<String, AveragedDatum>>();
    // Avg. for like frame updates that are too numerous to send directly.
    public static void averageUITime( String name, String label, long startTime ) {

        AveragedDatum data;

        HashMap<String, AveragedDatum> labels = averagedValues.get(name);
        if (labels == null) {
            labels = new HashMap<String, AveragedDatum>();
            averagedValues.put(name, labels);
        }

        data = labels.get(label);
        if (data == null) {
            data = new AveragedDatum();
            labels.put(label, data);
        }

        data.totalValue += timeSpent(startTime);
        data.numPoints += 1;

        synchronized (data) {
            if (data.numPoints >= 1000) {
                sendRawUITime(name, label, (long) (data.totalValue / data.numPoints));

                data.numPoints = 0;
                data.totalValue = 0;
            }
        }
    }
    // sendUITime for stuff that happens as direct user input, and should be individually tracked.
	public static void sendUITime( String name, String label, long startTime ) {
	    sendTime( "UITiming", name, label, startTime );
	}
    public static void sendRawUITime( String name, String label, long timeSpent ) {
        sendRawTime( "UITiming", name, label, timeSpent );
    }
    // Input and output in ms
    public static long timeSpent(long startTime) {
        return (System.nanoTime() / 1000 - startTime);
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
