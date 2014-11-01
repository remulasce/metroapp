package com.remulasce.lametroapp.pred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.remulasce.lametroapp.LaMetroUtil;


/* You make a stop prediction. It gets all the arrivals at a stop, based
 * on input. Etc.
 */
public class StopPrediction extends Prediction {
	protected final int MINIMUM_UPDATE_INTERVAL = 5000;
	protected final int INTERVAL_INCREASE_PER_SECOND = 100;
	
	
	protected String stopID;
	//if available
	protected String routeName;
	protected TripUpdateCallback callback;
		
	Map<String, Arrival> directionMap = new HashMap<String, Arrival>();
	
	Arrival firstArrival;
	Trip firstTrip;
	
	long lastUpdate;
	
	public StopPrediction(String stopID, String routeName) {
		this.stopID = stopID;
		this.routeName = routeName;
		this.firstArrival = new Arrival();
		this.firstTrip = new Trip();
	}
	
	@Override
	public void startPredicting() {
		PredictionManager.getInstance().startTracking(this);
	}

	@Override
	public void stopPredicting() {
		PredictionManager.getInstance().stopTracking(this);
	}

	@Override
	public void setTripCallback(TripUpdateCallback callback) {
		this.callback = callback;
		
	}

	@Override
	public String getRequestString() {
		return LaMetroUtil.makePredictionsRequest(stopID, routeName);
	}
	
	public String getStop() {
		return stopID;
	}

	@Override
	public long getTimeSinceLastUpdate() {
		return System.currentTimeMillis() - lastUpdate;
	}

	protected boolean arrivalTracked( Arrival a ) {
		if (!LaMetroUtil.isValidRoute(routeName)) { return true; }
		if (a.getRoute().equals(routeName)) { return true; }
		
		return false;
	}
	
	@Override
	public void handleResponse(String response) {
		lastUpdate = System.currentTimeMillis();
		
		List<Arrival> arrivals = LaMetroUtil.parseAllArrivals( response );
		
		for (Arrival newA : arrivals) {
			if ( !arrivalTracked( newA )) { continue; } 
			Arrival a = directionMap.get(newA.getDirection());
			if (a == null) {
				directionMap.put(newA.getDirection(), newA);
				a = newA;
			}
			else {
				a.setEstimatedArrivalSeconds(newA.getEstimatedArrivalSeconds());
			}
			callback.tripUpdated(a.getFirstTrip());
		}
	}

	@Override
	public void setUpdated() {
		this.lastUpdate = System.currentTimeMillis();
		
	}

	public String getRouteName() {
		return routeName;
	}
	
	protected Arrival firstArrival() {
		Arrival first = null;
		for (Arrival a : directionMap.values()) {
			if (first == null || a.getEstimatedArrivalSeconds() < first.getEstimatedArrivalSeconds())
			{
				if (a.getEstimatedArrivalSeconds() != -1) {
					first = a;
				}
			}
		}
		return first;
	}
	
	@Override
	public int getRequestedUpdateInterval() {
		Arrival first = firstArrival();
		int firstTime;
		
		if (first == null) {
			firstTime = 15;
		} else {
			firstTime = first.getEstimatedArrivalSeconds();
		}
		
		return Math.max(MINIMUM_UPDATE_INTERVAL, firstTime * INTERVAL_INCREASE_PER_SECOND);
	}

}
