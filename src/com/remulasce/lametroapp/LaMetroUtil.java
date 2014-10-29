package com.remulasce.lametroapp;

public class LaMetroUtil {

    static boolean isValidStop( String stop ) {
    	// I don't really know how to define this.
    	return stop != null && !stop.isEmpty();
    }
    static boolean isValidRoute( String route ) {
    	try {
    		int routeNum = Integer.valueOf(route);
    		return routeNum > 0 && routeNum < 1000;
    	} catch (Exception e){
    		return false;
    	}
    }
	
	
}
