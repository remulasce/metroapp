package com.remulasce.lametroapp.dynamic_data;

import com.remulasce.lametroapp.analytics.Log;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.platform_support.Network;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PredictionManager fetches updates from NexTrip and feeds the results back to the Predictions.
 *
 * It mainly just checks how long it's been since each Prediction has been updated, against how
 * frequently the Prediction wants to be updated.
 *
 * Predictions helpfully just have a getRequestString and handleResponse method.
 * So we don't have to do any parsing here, just feed Predictions with an update every so often.
 *
 * It doesn't do any fancy consolidation or staggering. It just has a minimum interval to prevent
 * ridiculously high requested frequencies, and it shuts down and restarts everything when the
 * app opens/closes.
 */


public class PredictionManager {
	private static final String TAG = "PredictionManager";
	private static final int UPDATE_INTERVAL = 5000;

	private static PredictionManager manager;
    private static NetworkStatusReporter statusReporter;

	public static PredictionManager getInstance() {
		if( manager == null ) { manager = new PredictionManager(); }
		return manager;
	}
    public static void setStatusReporter( NetworkStatusReporter reporter ) {
        statusReporter = reporter;
    }
	

	private final List<Prediction> trackingList = new CopyOnWriteArrayList<Prediction>();
	private UpdateStager updater;

    /** Start getting updates for this Prediction.
     * Starts the entire PredictionManager if it is not yet started.
     * An update will be forced in startUpdatingIfNotStarted, bypassing the normal wait period.
     *
     * @param p prediction to get updates for, via getRequestString and handleResponse
     */
	public void startTracking( Prediction p ) {
        if (!trackingList.contains(p)) {
            trackingList.add(p);
        }

        startUpdatingIfNotStarted();
	}

    /** Stops tracking a particular prediction
     * Doesn't shut down the service when empty.
     *
     * @param p The prediction to stop getting updates for.
     */
    public void stopTracking( Prediction p ) {
        trackingList.remove(p);
    }


    /**
     * Globally stops tracking on all predictions
     * This is for when the app is closed.
     * Resume all tracking with resumeTracking
     * The predictions are maintained while paused, just a new updater will need to be created.
     */
    public void pauseTracking() {
        synchronized (this) {
            Log.d(TAG, "Pausing all prediction tracking");

            pauseUpdating();
        }
    }

    /**
     * Globally resume tracking on all predictions
     * Creates a new thread to do that, if necessary.
     *
     * Also immediately unlocks the wait object to begin updating immediately.
     */
    public void resumeTracking() {
        synchronized (this) {
            resumeUpdating();
        }

        synchronized (updater.updateObject) {
            forceUpdateNow();
        }
    }

    /** The update thread wait()s on the updateObject for xxx ms.
     * When the object is notify()ed, the wait immediately ends
     * This lets you bypass the waiting period.
     */
    private void forceUpdateNow() {
        updater.updateObject.notify();
    }

    // Raw methods for pausing/resuming all tracking.
    private void resumeUpdating() {
        Log.d(TAG, "Resuming all prediction tracking");
        if (updater == null) {
            updater = new UpdateStager();
            new Thread(updater, "Prediction Update Checker").start();
        } else {
            Log.w(TAG, "Resuming an existing prediction updater");
        }
    }

    private void startUpdatingIfNotStarted() {
        synchronized (this) {
            if (updater == null) {
                updater = new UpdateStager();
                new Thread(updater, "Prediction Update Checker").start();
            }
        }

        synchronized (updater.updateObject) {
            forceUpdateNow();
        }
    }
    private void pauseUpdating() {
        if (updater != null) {
            updater.run = false;
            updater = null;
        } else {
            Log.w(TAG, "Pausing a missing prediction updater");
        }
    }

    /** This just compares requestedUpdateInterval vs time since last update on all the predictions.
     * It starts threads to do each actual net update.
     */
	class UpdateStager implements Runnable {
		public boolean run = true;
        public final Object updateObject = new Object();

		@Override
		public void run() {
			while (run) {
                updateOldPredictions();

                waitForNextRun();
            }
		}

        private void waitForNextRun() {
            try {
                // updateObject.notify() also breaks out of this statement.
                synchronized (updateObject) {
                    updateObject.wait(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void updateOldPredictions() {
            for (int i = trackingList.size() - 1; i >= 0; i--) {
                try {
                    Prediction p = trackingList.get(i);

                    int requestedInterval = p.getRequestedUpdateInterval();
                    long timeSinceUpdate = p.getTimeSinceLastUpdate();
                    if (timeSinceUpdate >= Math.max(requestedInterval, UPDATE_INTERVAL)) {
                        Log.v(TAG, "Getting update after " + requestedInterval);
                        p.setGettingUpdate();
                        GetUpdate(p);
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.w(TAG, "Prediction removed out from under PredictionManager");
                }
            }
        }

        private void GetUpdate(Prediction p) {
            PredictionFetcher r = new PredictionFetcher( p );
            new Thread(r, "Prediction update "+p.getRequestString()).start();
        }
    }
	

	
	class PredictionFetcher implements Runnable {
		final Prediction prediction;

		public PredictionFetcher(Prediction p) {
			this.prediction = p;
		}

		@Override
		public void run() {
            long t = Tracking.startTime();

			String request = prediction.getRequestString();
			Log.v(TAG, "Handling request "+request);
			
			String response = sendRequest( request );
			
			Log.v(TAG, "Response received: "+response);
			prediction.handleResponse(response);
			
			prediction.setUpdated();

			Tracking.sendTime("PredictionManager", "UpdateRunner", "Total Run", t);
		}

		public String sendRequest( String request ) {
            return Network.sendHTTPRequest( request, statusReporter );
		}
	}
}
