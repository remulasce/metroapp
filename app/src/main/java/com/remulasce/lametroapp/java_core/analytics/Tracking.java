package com.remulasce.lametroapp.java_core.analytics;


import java.util.HashMap;

public class Tracking {

    private static Tracking t;

    // in nanoseconds (1/Billionth second) from nanotime
	public static long startTime() {
	    return System.nanoTime();
	}


    public static class AveragedDatum {
        public double totalValue = 0;
        public double numPoints = 0;
    }

    private static final HashMap<String, HashMap<String, AveragedDatum>> averagedValues = new HashMap<String, HashMap<String, AveragedDatum>>();
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

    public static void setTracker(Tracking tracker) {
        t = tracker;
    }

    public static void setScreenName(String name) {
        if (t != null) {
            t.do_setScreenName(name);
        }
    }

    public static void sendEvent(String category, String action) {
        if (t != null) {
            t.do_sendEvent(category, action, null);
        }
    }
    public static void sendEvent(String category, String action, String label) {
        if (t != null) {
            t.do_sendEvent(category, action, label);
        }
    }


    // sendUITime for stuff that happens as direct user input, and should be individually tracked.
	public static void sendUITime( String name, String label, long startTime ) {
	    sendTime( "UITiming", name, label, startTime );
	}
    public static void sendRawUITime( String name, String label, long timeSpent ) {
        sendRawTime( "UITiming", name, label, timeSpent );
    }
    // Input in nanoseconds, output in millis
    public static long timeSpent(long startTime) {
        return (System.nanoTime() - startTime) / 1000000;
    }
    public static void sendTime( String category, String name, String label, long startTime) {
        sendRawTime( category, name, label, timeSpent( startTime ) );
    }
	private static void sendRawTime(String category, String name, String label, long timeSpent) {
	    Log.v(category, name+" "+label+": "+timeSpent);

        if (t != null) {
            t.do_sendRawTime(category, name, label, timeSpent);
        }
	}


    public void do_setScreenName(String name) {}
    public void do_sendRawTime(String category, String name, String label, long timeSpent) {}
    public void do_sendEvent(String category, String action, String label) {}
}
