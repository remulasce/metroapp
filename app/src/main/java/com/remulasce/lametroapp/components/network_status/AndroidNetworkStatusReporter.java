package com.remulasce.lametroapp.components.network_status;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

/**
 * Created by Remulasce on 1/26/2015.
 *
 * This thing listens to PredictionManager reporting network failures, and displays it on the main screen.
 */
public class AndroidNetworkStatusReporter implements NetworkStatusReporter {

    private final View statusBar;
    private final TextView textView;

    private final Handler uiHandler;

    public AndroidNetworkStatusReporter(View statusBar) {
        this.statusBar = statusBar;
        this.textView = (TextView) statusBar.findViewById(R.id.network_status_text);

        this.uiHandler = new Handler( Looper.getMainLooper());
    }

    @Override
    public void reportFailure() {
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

    @Override
    public void reportGettingUpdate() {
        uiHandler.post( new Runnable() {
            @Override
            public void run() {
                statusBar.setVisibility(View.VISIBLE);
                textView.setText("Getting update...");
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
