package com.remulasce.lametroapp.dynamic_data.types;

import java.io.Serializable;
import java.util.List;



/** Base class for anything we're requesting
 * 
 * @author Fintan
 *
 */
public abstract class Prediction implements Serializable{
	public abstract void startPredicting();
	public abstract void stopPredicting();
	
	//ms
	public abstract int getRequestedUpdateInterval();
	public abstract long getTimeSinceLastUpdate();
	public abstract void setUpdated();
	public abstract void setGettingUpdate();
	
	public abstract String getRequestString();
	public abstract void handleResponse(String response);
	
	public abstract void setTripCallback( TripUpdateCallback callback );
}
