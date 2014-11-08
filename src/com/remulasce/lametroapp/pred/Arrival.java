package com.remulasce.lametroapp.pred;

import java.util.List;

import com.remulasce.lametroapp.LaMetroUtil;


/** One arrival is one route-direction's arrival at one stop.
 * This is the building block of all information
 * because it's the only way to get realtime 
 * predictions.
 */
public class Arrival {

	String route;
	String direction;
	String stopID;
	
	// Nextrip's most recent prediction for when this thing arrives
	// both in ms since epoch, for sub-second refresh frequency
	long lastPrediction;
	long lastUpdate;
	
	int vehicleNum;
	
	Trip firstTrip;
	
	public Arrival() {
		firstTrip = new ArrivalTrip(this);
	}
	
	/** In seconds from now */
	public int getEstimatedArrivalSeconds() {
		return (int) Math.max(0, (lastPrediction - System.currentTimeMillis()) / 1000);
	}
	public void setEstimatedArrivalSeconds( int secondsTillArrival ) {
		lastPrediction = System.currentTimeMillis() + secondsTillArrival*1000;
		lastUpdate = System.currentTimeMillis();
	}
	public void setRoute( String route ) {
		this.route = route;
	}
	public void setVehicleNum(int num) {
		this.vehicleNum = num;
	}
	//In ms
	public long getTimeSinceLastEstimation() {
		return System.currentTimeMillis() - lastUpdate;
	}
	
	public void updated() {
		
	}
	
	public void setDirection(String s) {
		this.direction = s;
	}
	public void setStopID( String stopID ) {
		if (!LaMetroUtil.isValidStop(stopID)) {
			return;
		}
		this.stopID = stopID;
	}
	
	public String getRoute() { return route; }
	public String getStopID() { return stopID; }
	public String getDirection() { return direction; }
	public int getVehicleNum() { return vehicleNum; }
	
	public Trip getFirstTrip() {
		return firstTrip;
	}
}
