package com.remulasce.lametroapp.components.network_status;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.remulasce.lametroapp.R;

/**
 * Created by Remulasce on 1/26/2015.
 *
 * This thing listens to PredictionManager reporting network failures, and displays it on the main screen.
 */
public class NetworkStatusReporter {

    View statusBar;
    TextView textView;

    Handler uiHandler;

    public NetworkStatusReporter( View statusBar ) {
        this.statusBar = statusBar;
        this.textView = (TextView) statusBar.findViewById(R.id.network_status_text);

        this.uiHandler = new Handler( Looper.getMainLooper());
    }

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
}
