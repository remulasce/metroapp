package com.remulasce.lametroapp.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.analytics.Tracking;

import java.util.HashMap;

/**
 * Created by Remulasce on 3/5/2015.
 *
 * It's basically a straight pass-through to Google Analytics.
 */
public class AndroidTracking extends Tracking {

    private Tracker t;
    public AndroidTracking( Context c ) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance( c );

        t = analytics.newTracker(R.xml.lametro_tracker);
        t.enableAdvertisingIdCollection(true);
        t.enableExceptionReporting(true);
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

    @Override
    public void do_sendEvent(String category, String action, String label) {
        t.send( new HitBuilders.EventBuilder()
                .setCategory( category )
                .setAction( action )
                .setLabel( label )
                .build() );
    }

    @Override
    public void do_setScreenName(String name) {
        t.setScreenName(name);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void do_sendRawTime(String category, String name, String label, long timeSpent) {
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
