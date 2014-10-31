package com.remulasce.lametroapp.pred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.remulasce.lametroapp.LaMetroUtil;

import android.util.Log;

public class PredictionManager {
	static final String TAG = "PredictionManager";
	static final int UPDATE_INTERVAL = 10000;
	
	static PredictionManager manager;
	public static PredictionManager getInstance() {
		if( manager == null ) { manager = new PredictionManager(); }
		return manager;
	}
	
	
	protected ArrayList<Prediction> trackingList = new ArrayList<Prediction>();
	protected UpdateStager updater;
	
	public void startTracking( Prediction p ) {
		if (!trackingList.contains(p)) {
			trackingList.add(p);
		}
		if (updater == null) {
			updater = new UpdateStager();
			new Thread(updater).start();
		}
	}
	
	
	public void pauseTracking() {
		Log.d(TAG, "Pausing all prediction tracking");
		if (updater != null) {
			updater.run = false;
			updater = null;
		}
	}
	public void resumeTracking() {
		Log.d(TAG, "Resuming all prediction tracking");
		if (updater == null) {
			updater = new UpdateStager();
			new Thread(updater).start();
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
			
				for (Prediction p : trackingList) {
					if (p.getTimeSinceLastUpdate() >= UPDATE_INTERVAL) {
						p.setUpdated();
						GetUpdate( p );
					}
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	protected void GetUpdate( Prediction p ) {
		RequestHandler r = new RequestHandler( p );
		new Thread(r).start();
	}
	
	protected class RequestHandler implements Runnable {
		Prediction prediction;

		
		public RequestHandler( Prediction p ) {
			this.prediction = p;
		}

		@Override
		public void run() {

			//String request = LaMetroUtil.makePredictionsRequest(prediction.getStopID(), prediction.getRoute());
			String request = prediction.getRequestString();
			Log.d(TAG, "Handling request "+request);
			// Consolidate all predictions that rely on this request, etc.
			
			String response = sendRequest( request );
			
			Log.d(TAG, "Response received: "+response);
			prediction.handleResponse(response);
			
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
					Log.e("RequestManager", "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}
		
	}
}
