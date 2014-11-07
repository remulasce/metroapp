package com.remulasce.lametroapp.pred;

import android.content.Context;
import android.os.Looper;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.MainActivity;

public class ArrivalTrip extends Trip {

	protected Arrival parentArrival;
	

	public ArrivalTrip( Arrival parentArrival ) {
		this.parentArrival = parentArrival;
	}
	
	public String toString() {
		if (parentArrival == null) {
			return "Invalid parent";
		}
		int seconds = parentArrival.getEstimatedArrivalSeconds();
		
		return 	parentArrival.getRoute() + ": "+ parentArrival.getDirection() + " \n"
				+ "Arriving in "+ LaMetroUtil.secondsToDisplay( seconds )
						+ " ("+seconds+"s)";		
	}
	

	public void executeAction(Context context) {
		int stopNum = Integer.valueOf(parentArrival.stopID);
		String route = parentArrival.route;
		String destination = parentArrival.direction;
		MainActivity.SetNotifyService(stopNum, route, destination, context);
	}
	
}

