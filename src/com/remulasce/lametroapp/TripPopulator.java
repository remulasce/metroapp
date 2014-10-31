package com.remulasce.lametroapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.remulasce.lametroapp.pred.StopPrediction;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.pred.TripUpdateCallback;

public class TripPopulator {
	private static final String TAG = "TripPopulator";
	enum TripDriver { NONE, ROUTE, STOP };
	
	protected ListView list;
	protected ArrayAdapter<Trip> adapter;
	protected List<Trip> activeTrips = new ArrayList<Trip>();
	protected List<Trip> inactiveTrips = new ArrayList<Trip>();
	
	protected TripDriver tripDriver = TripDriver.NONE;
	
	protected Handler uiHandler;
	protected UpdateRunner updateRunner;
	protected Thread updateThread;
	protected boolean running = false;
	
	protected String routeName;
	protected String stopName;
	
	protected boolean updatesAvailable = true;
	
	public TripPopulator( ListView list ) {
		this.list = list;
		this.uiHandler = new Handler(Looper.getMainLooper());
		
		adapter = new ArrayAdapter<Trip>(list.getContext(), android.R.layout.simple_list_item_1);
		list.setAdapter(adapter);
	}
	
	
	public void StartPopulating() {
		if (running) {
			Log.w(TAG, "Started an already-populating populator");
			return;
		}
		
		updateRunner = new UpdateRunner();
		updateThread = new Thread(updateRunner);
		
		updateThread.start();
		
	}
	
	public void StopPopulating() {
		updateRunner.run = false;
	}
	
	public void RouteSelectionChanged (String routeName) {
		Log.d(TAG, "Route changed: "+routeName);
		
		this.routeName = routeName;
		updatesAvailable = true;
		
	}
	public void StopSelectionChanged (String stopName) {
		Log.d(TAG, "Stop changed: "+stopName);
		
		this.stopName = stopName;
		updatesAvailable = true;
	
	}
	
	
	protected class UpdateRunner implements Runnable {
		protected boolean run = false;
		
		
		protected StopPrediction stopPrediction;
		//protected RoutePrediction routePrediction
		
		protected void updateRoute( int route ) {
			tripDriver = TripDriver.ROUTE;
			
			Log.d(TAG, "Updating based on route");
		}
		protected void updateStop( String stop ) {
			tripDriver = TripDriver.STOP;
			
			if (stopPrediction != null) {
				Log.d(TAG, "Updating stop prediction");
				activeTrips.clear();
				
				uiHandler.post( new Runnable() {
					@Override
					public void run() {
						adapter.clear();	
					}
				});
				
				stopPrediction.stopPredicting();
			}
				
			stopPrediction = new StopPrediction( stop, null );
			stopPrediction.setTripCallback(callback);
			
			stopPrediction.startPredicting();
		
			
			
			Log.d(TAG, "Updating based on stop");
			
		}
		
		protected TripUpdateCallback callback = new TripUpdateCallback() {
			@Override
			public void tripUpdated(final Trip trip) {
				
				uiHandler.post( new Runnable() {

					@Override
					public void run() {
						if (inactiveTrips.contains(trip)) {
							Log.d(TAG, "Skipped old trip callback");
							return;
						}
						
						if (!activeTrips.contains(trip)) {
							activeTrips.add(trip);
							adapter.add(trip);
						}
						
						// Update UI
						adapter.notifyDataSetChanged();
					}
					
				});
				

			}
		};
		
		@Override
		public void run() {
			run = true;
			Log.d(TAG, "UpdateRunner starting");
			
			while (run) {
				
				if (updatesAvailable) {

					boolean validRoute = LaMetroUtil.isValidRoute( routeName );
					boolean validStop = LaMetroUtil.isValidStop( stopName );

					switch (tripDriver) {
					case NONE:
						if (validRoute) {
							updateRoute( Integer.valueOf( routeName ) );
							break;
						}
						if (validStop) {
							updateStop( stopName );
							break;
						}
						break;
					case ROUTE:
						if (validRoute) {
							updateRoute( Integer.valueOf( routeName ) );
							break;
						}
						break;
					case STOP:
						if (validStop) {
							updateStop( stopName );
							break;
						}
						break;
					
					}
					
					updatesAvailable = false;
					// Sync error here ^
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {}
				}
				
			}
			Log.d(TAG, "UpdateRunner ending");
			
		}
		
	}
}
