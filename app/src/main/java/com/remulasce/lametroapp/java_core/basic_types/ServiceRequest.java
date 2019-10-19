package com.remulasce.lametroapp.java_core.basic_types;

import android.location.Location;

import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Remulasce on 12/16/2014.
 *
 * One (or maybe more) servicerequest should be created for each input into the omnibar
 * Its job is to create the subsequent data types and get them started tracking
 *
 * There's not much top-down control, mainly it just starts them up and lets it go.
 */
public abstract class ServiceRequest implements Serializable {
    String displayName = "ServiceRequest";
    private boolean inScope = true;


    public ServiceRequest() {}
    public ServiceRequest(String s) {
        this.displayName = s;
    }

    // Lifecycle.
    public enum RequestLifecycleState {
        STOPPED,
        PAUSED,
        RUNNING
    }
    // Cancel on final kill. Cancel is not recoverable.
    // Use Start for both initial start and post-pause resume.
    //   So you should try not to remake stuff if it's already been made.
    // Pause/resume are basically used in serialization. So resume
    //   needs to make sure /everything/ it needs is set up.
    //   Specifically, that its predictions are getting updated, since the
    //      entire activity may have been killed since pause.
//    public void start(){ inScope = true; }
    // eg. start request
    public void startRequest() { lifecycleState = RequestLifecycleState.RUNNING; }
    public void pauseRequest() { lifecycleState = RequestLifecycleState.PAUSED; }
    public void cancelRequest() { lifecycleState = RequestLifecycleState.STOPPED; }

    protected RequestLifecycleState lifecycleState = RequestLifecycleState.STOPPED;
    public RequestLifecycleState getLifecycleState() { return lifecycleState; }

    // Old lifecycle. Probably doesn't do anything any more.
    public void descope() {
        inScope = false;
    }
    public boolean isInScope() {
        return inScope;
    }

    // Returns a collection of Locations that should be prioritized in autocomplete.
    // Pretty much, a Stop should prioritize other Stops near it, for transfers.
    public abstract Collection<BasicLocation> getInterestingLocations();

    // Returns a collection of Stops that are currently being tracked by this ServiceRequest.
    public abstract Collection<Stop> getStops();

    // Platform UI layer uses this hook to do all its display, via ServiceRequestHandler
    public abstract Collection<Trip> getTrips();

    // Trips produced by Requests can be swiped away. This should restore all of them.
    // It's kind of weird that restoring them is handled at the ServiceRequest, but dismissing
    //   is handled by the Trip itself.
    public abstract void restoreTrips();

    // Check if this serviceRequest is totally unreasonable or not.
    // Subclasses do more useful checks than this.
    // eg. StopServiceRequest makes sure it has a stop to track
    public boolean isValid() {
        if ( displayName == null || displayName.isEmpty()) { return false; }

        return true;
    }

    // These were useful early in development. Might be nice for testing.
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() { return displayName; }

    // This was supposed to make the looping threads update immediately when stuff changed.
    // They do this in some cases, but based on other factors. These can go away.
    public abstract boolean updateAvailable();
    public abstract void updateTaken();

    // This was used to decide whether to show the "loading" progress circle spinner.
    // So a request that was just added would return true and get a spinner while it loaded
    // But a request which had all its trips dismissed wouldn't.
    // It never quite worked, and sure doesn't now.
    public abstract boolean hasTripsToDisplay();

    // This was used for serialization, because serialization used to be stupid simple.
    // Now we just serialize the whole thing, which is easier for big request types.
    public abstract Collection<String> getRaw();

    @Override
    public String toString() {
        return displayName;
    }
}
