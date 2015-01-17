package com.remulasce.lametroapp;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.Vehicle;

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
                                         Vehicle vehicle, Context context ) {

        Tracker t = Tracking.getTracker(context);
        Intent i = new Intent( context, ArrivalNotifyService.class );

        if ( !stop.isValid() ) {
            t.send( new HitBuilders.EventBuilder()
                    .setCategory( "NotifyService" )
                    .setAction( "SetNotifyService" )
                    .setLabel( "Stop invalid" ).build() );
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

            context.stopService( i );
            context.startService( i );

            t.send( new HitBuilders.EventBuilder().setCategory( "NotifyService" )
                    .setAction( "SetNotifyService" ).build() );
        } catch ( IllegalArgumentException e ) {
            t.send( new HitBuilders.EventBuilder().setCategory( "NotifyService" )
                    .setAction( "Bad input to notify service" ).build() );
        }
    }

    public static void stopNotifyService(Context context) {
        Intent i = new Intent( context, ArrivalNotifyService.class );
        Tracker t = Tracking.getTracker(context);

        t.send( new HitBuilders.EventBuilder()
                .setCategory( "NotifyService" )
                .setAction( "NotifyService Stop Button" )
                .build() );

        context.stopService( i );
    }
}
