package com.remulasce.lametroapp;

import android.content.Context;
import android.content.Intent;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.Vehicle;

/**
 * Created by Remulasce on 1/13/2015.
 *
 * Handles starting the NotifyService
 * You'd expect it to do some actual managing, given its name
 * But actually it just has a helper fxn for now.
 * It's better than using MainActivity to do the same thing, I guess.
 *
 * But man, static is like as bad as global.
 *
 */
public class NotifyServiceManager {
    public static void SetNotifyService( Stop stop, Route route, Destination destination,
                                         Vehicle vehicle, int secondsToNotify, Context context ) {

        Intent i = new Intent( context, ArrivalNotifyService.class );

        if ( !stop.isValid() ) {
            Tracking.sendEvent("NotifyService", "SetNotifyService", "Stop invalid");

            return;
        }

        try {
            i.putExtra( "Agency", LaMetroUtil.getAgencyFromRoute( route, stop ) );
            i.putExtra( "StopID", stop.getNum() );

            if ( destination != null && destination.isValid() ) {
                i.putExtra( "Destination", destination.getString() );
            }
            if ( vehicle != null && vehicle.isValid() ) {
                i.putExtra( "VehicleNumber", vehicle.getString() );
            }
            if ( route != null && route.isValid() ) {
                i.putExtra( "Route", route.getString() );
            }

            if (secondsToNotify < 0) {
                secondsToNotify = 90;
            }

            i.putExtra("NotificationTime", secondsToNotify);

            context.stopService( i );
            context.startService( i );

            Tracking.sendEvent("NotifyService", "SetNotifyService");
        } catch ( IllegalArgumentException e ) {
            Tracking.sendEvent("NotifyService", "Bad input to notify service");
        }
    }

    public static void stopNotifyService(Context context) {
        Intent i = new Intent( context, ArrivalNotifyService.class );

        Tracking.sendEvent("NotifyService", "NotifyService Stop Button");

        context.stopService( i );
    }
}
