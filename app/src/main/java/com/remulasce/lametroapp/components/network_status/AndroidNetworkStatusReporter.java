package com.remulasce.lametroapp.components.network_status;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

/**
 * Created by Remulasce on 1/26/2015.
 *
 * This thing listens to PredictionManager reporting network failures, and displays it on the main screen.
 */
public class AndroidNetworkStatusReporter implements NetworkStatusReporter {

    private View statusBar;
    private TextView textView;

    private Handler uiHandler;

    public AndroidNetworkStatusReporter(View statusBar) {
        if (statusBar == null) {
            Log.w("AndroidStatusReporter", "Null statusBar sent to report to");

            return;
        }

        this.statusBar = statusBar;
        this.textView = (TextView) statusBar.findViewById(R.id.network_status_text);

        this.uiHandler = new Handler( Looper.getMainLooper());
    }

    @Override
    public void reportFailure() {
        if (uiHandler == null || textView == null || statusBar == null) {
            Log.w("AndroidStatusReporter", "No statusBar sent to report to");
            return;
        }

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                statusBar.setVisibility(View.VISIBLE);
                textView.setText("Network failure");
            }
        });
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusBar.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }
}
