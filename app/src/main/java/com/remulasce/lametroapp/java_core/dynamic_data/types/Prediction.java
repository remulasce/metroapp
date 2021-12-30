package com.remulasce.lametroapp.java_core.dynamic_data.types;

import java.io.Serializable;

/**
 * Base class for anything we're requesting Usage: ServiceRequest makes one or several Predictions
 * Each subclass of Prediction returns one Nextrip-style string format It does whatever it wants
 * with the response And it makes Trips to do UI for it, which it puts through its callback.
 */
public abstract class Prediction implements Serializable {

  public enum PredictionState {
    PAUSED, // Not tracking, because we shouldn't be.
    FETCHING, // No arrivals, but going to network for first time to find some.
    GOOD, // The arrivals we show is what actually existed at some point.
    CACHED, // Our arrivals are still good, but our last network pull failed.
    // This is for going down into 7th/Metro on the Blue Line- 100% network status
    // is not expected, so cached is not a failure case.
    // The user probably understands what's happening.
    BAD // We have no data and can't contact network.
  }

  PredictionState predictionState = PredictionState.PAUSED;

  long lastUpdate;
  boolean inUpdate = false;

  boolean inScope = false;
  boolean needsQuickUpdate = false;

  // State used to tell if network failed or there's just no arrivals.
  public PredictionState getPredictionState() {
    return predictionState;
  }

  // Start/stop for live updates
  public abstract void startPredicting();

  public abstract void stopPredicting();

  // Restores all trips that would have been swipe-dismissed
  public abstract void restoreTrips();

  // It's really more of a shutdown mechanism. Only called when a stop is removed from list.
  public abstract void cancelTrips();

  // ms
  public abstract int getRequestedUpdateInterval();

  public abstract long getTimeSinceLastUpdate();

  // Don't tell us to update if we're already updating you
  public void setUpdated() {
    synchronized (this) {
      inUpdate = false;
      needsQuickUpdate = false;

      this.lastUpdate = System.currentTimeMillis();
    }
  }

  public void setGettingUpdate() {
    synchronized (this) {
      if (predictionState == PredictionState.PAUSED) {
        predictionState = PredictionState.FETCHING;
      }
      inUpdate = true;
    }
  }

  // PredictionManager uses these.
  public abstract String getRequestString();

  public void handleResponse(String response) {
    predictionState = PredictionState.GOOD;
    lastUpdate = System.currentTimeMillis();
  }

  // Replaces StartPredicting, without trying to actually get the manager singleton.
  public void setTestMode() {
    inScope = true;
  }
}
