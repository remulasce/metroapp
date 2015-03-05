package com.remulasce.lametroapp.analytics;

import android.content.Context;
import android.util.*;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.R;

import java.util.HashMap;

/**
 * Created by Remulasce on 3/5/2015.
 */
public class AndroidTracking extends Tracking {

    private Tracker t;
    public Tracker do_getTracker( Context c ) {
        if (t == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance( c );

            t = analytics.newTracker(R.xml.lametro_tracker);
            t.enableAdvertisingIdCollection(true);
        }

        return t;
    }

    // in nanoseconds (1/Billionth second) from nanotime
    public static long do_startTime() {
        return System.nanoTime();
    }


    class AveragedDatum {
        public double totalValue = 0;
        public double numPoints = 0;
    }

    private final HashMap<String, HashMap<String, AveragedDatum>> averagedValues = new HashMap<String, HashMap<String, AveragedDatum>>();
    // Avg. for like frame updates that are too numerous to send directly.
    public void do_averageUITime( String name, String label, long startTime ) {

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

        if (data.numPoints >= 1000) {
            synchronized (data) {
                if (data.numPoints > 0) {
                    sendRawUITime(name, label, (long) (data.totalValue / data.numPoints));
                }

                data.numPoints = 0;
                data.totalValue = 0;
            }
        }
    }
    // sendUITime for stuff that happens as direct user input, and should be individually tracked.
    public void do_sendUITime( String name, String label, long startTime ) {
        sendTime( "UITiming", name, label, startTime );
    }
    public void do_sendRawUITime( String name, String label, long timeSpent ) {
        do_sendRawTime( "UITiming", name, label, timeSpent );
    }
    // Input in nanoseconds, output in millis
    public long do_timeSpent(long startTime) {
        return (System.nanoTime() - startTime) / 1000000;
    }
    public void do_sendTime( String category, String name, String label, long startTime) {
        do_sendRawTime( category, name, label, timeSpent( startTime ) );
    }
    private void do_sendRawTime(String category, String name, String label, long timeSpent) {
        android.util.Log.v(category, name + " " + label + ": " + timeSpent);

        if (t == null) {
            android.util.Log.w(category, "No tracker set, unable to send analytics");
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
