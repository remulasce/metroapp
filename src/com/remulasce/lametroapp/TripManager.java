package com.remulasce.lametroapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class TripManager {

	static TripManager manager;
	static TripManager getInstance() {
		if( manager == null ) { manager = new TripManager(); }
		return manager;
	}
	
	
	//public void 
	
	
	public void submitRawRequest( String request, NetRequestCallback callback, int context ) {
		
	}
	
	
	
	protected class RequestHandler implements Runnable {
		String request;
		NetRequestCallback callback;
		int context;

		
		public RequestHandler( String request, NetRequestCallback callback, int context ) {
			this.request = request; this.callback = callback; this.context = context;
		}

		@Override
		public void run() {

			String response = sendRequest( request );
			
			
			callback.requestComplete(request, response, context);
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
