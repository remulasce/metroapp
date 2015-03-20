package com.remulasce.lametroapp.java_core.dynamic_data.types;

import java.io.Serializable;


/** Base class for anything we're requesting
 * Usage:
 * ServiceRequest makes one or several Predictions
 * Each subclass of Prediction returns one Nextrip-style string format
 * It does whatever it wants with the response
 * And it makes Trips to do UI for it, which it puts through its callback.
 * 
 * @author Fintan
 *
 */
public abstract class Prediction implements Serializable{
    // Start/stop for live updates
	public abstract void startPredicting();
	public abstract void stopPredicting();

    // Restores all trips that would have been swipe-dismissed
    public abstract void restoreTrips();

    // It's really more of a shutdown mechanism. Only called when a stop is removed from list.
    public abstract void cancelTrips();

    // I think this was supposed to do something at some point.
    public abstract boolean isInScope();

    // Unused, basically was used to check when to display the 'fetching updates' spinner
    public abstract boolean hasAnyPredictions();
	
	// ms
	public abstract int getRequestedUpdateInterval();
	public abstract long getTimeSinceLastUpdate();

    // Don't tell us to update if we're already updating you
	public abstract void setUpdated();
	public abstract void setGettingUpdate();

    // PredictionManager uses these.
	public abstract String getRequestString();
	public abstract void handleResponse(String response);
}
