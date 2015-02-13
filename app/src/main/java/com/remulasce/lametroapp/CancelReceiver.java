package com.remulasce.lametroapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Fintan on 1/18/2015.
 */
public class CancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("CancelReceiver", "OnReceive");
        int notificationId = 294;


        // if you want cancel notification
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(notificationId);
        NotifyServiceManager.stopNotifyService(context);
    }
}
