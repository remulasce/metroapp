package com.remulasce.lametroapp.java_core.dynamic_data.types;

/**
 * Created by Remulasce on 4/21/2015.
 *
 * This trip conveys special status about active ServiceRequests.
 * It lets the user know when the request is going to network to get predictions
 *   or when an error has occured.
 *
 * It should only be shown when those conditions arise.
 *
 * Conditions are:
 * - Fetching update from server
 * - Error occured from server.
 */
public class RequestStatusTrip extends Trip{
}
