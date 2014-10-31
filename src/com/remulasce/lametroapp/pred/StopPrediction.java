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

	protected String stopID;
	//if available
	protected String routeName;
	protected TripUpdateCallback callback;
	
	List<Trip> stopTrips = new ArrayList<Trip>();
	
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

	@Override
	public long getTimeSinceLastUpdate() {
		return System.currentTimeMillis() - lastUpdate;
	}

	@Override
	public void handleResponse(String response) {
		lastUpdate = System.currentTimeMillis();
		
		List<Arrival> arrivals = LaMetroUtil.parseAllArrivals( response );
		
		for (Arrival newA : arrivals) {
			Arrival a = directionMap.get(newA);
			if (a == null) {
				directionMap.put(newA.getDirection(), newA);
				a = newA;
			}
			else {
				a.setEstimatedArrivalSeconds(newA.getEstimatedArrivalSeconds());
			}
			callback.tripUpdated(a.getFirstTrip());
		}
		/*
		for (Arrival a : arrivals) {
			for (Trip t : stopTrips) {
				if (t)
			}
		}
		*/
		
		/*
		
		LaMetroUtil.parseFirstArrival(firstArrival, response);
		
		if (firstArrival.direction != null && !firstArrival.direction.isEmpty() && firstArrival.getEstimatedArrivalSeconds() != -1) {
			firstTrip.setText( firstArrival.getDirection() + " \n" + "Arriving in: "+ firstArrival.getEstimatedArrivalSeconds() +"s" );
			
			callback.tripUpdated(firstTrip);			
		}
		*/
		
		//firstArrival.get
		// Parse out all of the individual trips
		// Callback each individually
		
		
	}

	@Override
	public void setUpdated() {
		this.lastUpdate = System.currentTimeMillis();
		
	}

}
