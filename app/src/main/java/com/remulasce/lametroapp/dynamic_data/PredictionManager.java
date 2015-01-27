package com.remulasce.lametroapp.dynamic_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;

public class PredictionManager {
	static final String TAG = "PredictionManager";
	static final int UPDATE_INTERVAL = 10000;
	
	static PredictionManager manager;
    static NetworkStatusReporter statusReporter;

	public static PredictionManager getInstance() {
		if( manager == null ) { manager = new PredictionManager(); }
		return manager;
	}
    public static void setStatusReporter( NetworkStatusReporter reporter ) {
        statusReporter = reporter;
    }
	

	protected final List<Prediction> trackingList = new CopyOnWriteArrayList<Prediction>();
	protected UpdateStager updater;
	
	public void startTracking( Prediction p ) {
        if (!trackingList.contains(p)) {
            trackingList.add(p);
        }
        synchronized (this) {
            if (updater == null) {
                updater = new UpdateStager();
                new Thread(updater, "Prediction Update Checker").start();
            }
        }
	}
	
	public void pauseTracking() {
		synchronized (this) {
			Log.d(TAG, "Pausing all prediction tracking");
			if (updater != null) {
				updater.run = false;
				updater = null;
			} else {
                Log.w(TAG, "Pausing a missing prediction updater");
            }
		}
	}
	public void resumeTracking() {
		synchronized (this) {
			Log.d(TAG, "Resuming all prediction tracking");
			if (updater == null) {
				updater = new UpdateStager();
				new Thread(updater, "Prediction Update Checker").start();
			} else {
                Log.w(TAG, "Resuming an existing prediction updater");
            }
		}
	}
	
	public void stopTracking( Prediction p ) {
        trackingList.remove(p);
	}
	
	protected class UpdateStager implements Runnable {
		public boolean run = true;
		@Override
		public void run() {
			while (run) {
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
                        continue;
                    }
                }

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
			}
		}
		
	}
	
	protected void GetUpdate( Prediction p ) {
		RequestHandler r = new RequestHandler( p );
		new Thread(r, "Prediction update "+p.getRequestString()).start();
	}
	
	protected class RequestHandler implements Runnable {
		Prediction prediction;

		public RequestHandler( Prediction p ) {
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
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			String URI = request;

			HttpGet httpGet = new HttpGet(URI);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
                    if (statusReporter != null) {
                        statusReporter.reportFailure();
                    }
					Log.e(TAG, "Failed to download file");
				}
			} catch (ClientProtocolException e) {
                if (statusReporter != null) {
                    statusReporter.reportFailure();
                }
				e.printStackTrace();
			} catch (IOException e) {
                if (statusReporter != null) {
                    statusReporter.reportFailure();
                }
				e.printStackTrace();
			}
			return builder.toString();
		}
	}
}
