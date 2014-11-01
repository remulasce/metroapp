package com.remulasce.lametroapp.pred;

import com.remulasce.lametroapp.LaMetroUtil;

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
				+ "Arriving in "+ LaMetroUtil.secondsToDisplay( seconds );
						//+ " ("+seconds+"s)";		
	}
	
	
}
