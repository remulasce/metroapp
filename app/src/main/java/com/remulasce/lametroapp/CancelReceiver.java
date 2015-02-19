package com.remulasce.lametroapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("CancelReceiver", "OnReceive");
        NotifyServiceManager.stopNotifyService(context);
    }
}
