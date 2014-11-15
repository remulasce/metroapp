package com.remulasce.lametroapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.pred.PredictionManager;
import com.remulasce.lametroapp.pred.Trip;


public class MainActivity extends ActionBarActivity {

	Button setButton;
	Button stopButton;
	EditText stopField;
	EditText routeField;
	EditText vehicleField;
	
	ListView tripList;
	TripPopulator populator;
	
	Tracker t;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAnalytics();
        
        linkViewReferences();
        setupActionListeners();

        setupDefaults(getIntent());
    }
    
    protected void linkViewReferences() {
        stopField		= (EditText) findViewById(R.id.idtext);
        routeField		= (EditText) findViewById(R.id.routetext);
        vehicleField	= (EditText) findViewById(R.id.vehicleNum);
        setButton		= (Button)   findViewById(R.id.setbutton);
        stopButton		= (Button)	 findViewById(R.id.stopbutton);
        
        tripList = (ListView) findViewById(R.id.tripList);
    }
    
    protected void setupActionListeners() {
    	
        setButton.setOnClickListener( setButtonListener );
        stopButton.setOnClickListener( stopButtonListener );
        
        stopField.addTextChangedListener(StopTextWatcher);
        routeField.addTextChangedListener(RouteTextWatcher);

        populator = new TripPopulator( tripList );
        
        tripList.setOnItemClickListener( tripClickListener );
    }
    
    protected OnItemClickListener tripClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Trip trip = (Trip)parent.getItemAtPosition(position);
			trip.executeAction(MainActivity.this);
		}
    }; 
    
    protected OnClickListener setButtonListener = new OnClickListener() {
    	public void onClick(View v) {
    		String stopText 	= stopField.getText().toString();
    		int stopnum 		= Integer.valueOf(stopText);
    		String vehicleText 	= vehicleField.getText().toString();
    		String route 		= routeField.getText().toString();

            t.send(new HitBuilders.EventBuilder()
                .setCategory("NotifyService")
                .setAction("NotifyService Set Button")
                .set( "params", stopText+stopnum+vehicleText+route )
                .build());
    		
    		SetNotifyService(stopnum, route, null, vehicleText, MainActivity.this);
    	}
    };
    protected OnClickListener stopButtonListener = new OnClickListener() {
    	public void onClick(View v) {
    		Intent i = new Intent(MainActivity.this, ArrivalNotifyService.class);
    		
    		t.send(new HitBuilders.EventBuilder()
            	.setCategory("NotifyService")
            	.setAction("NotifyService Stop Button")
            	.build());
    		
    		MainActivity.this.stopService(i);
    	}
    };
    
    protected void startAnalytics() {
    	t = Tracking.getTracker(getApplicationContext());
    }
    
    protected void setupDefaults(Intent bundle) {
    	String route = bundle.getStringExtra("Route");
    	String stop  = bundle.getStringExtra("StopID");
    	String veh   = bundle.getStringExtra("VehicleNumber");
    	
    	if (route != null) {
    		routeField.setText(route);
    	}
    	if (stop != null) {
    		stopField.setText(stop);
    	}
    	if (veh != null) {
    		vehicleField.setText(veh);
    	}
    	
        t.send(new HitBuilders.EventBuilder()
            .setCategory("MainScreen")
            .setAction("Field Population")
            .setLabel( "First Run" )
            .set( "route", route )
            .set( "stop", stop)
            .set( "vehicle", veh )
            .build());
    	
    	populator.StopSelectionChanged(stopField.getText().toString());
    }
    
	public static void SetNotifyService(int stopnum, String route, String destination, String vehicleNumber, Context context) {
		Intent i = new Intent(context, ArrivalNotifyService.class);

		i.putExtra("Agency", LaMetroUtil.getAgencyFromRoute(route, stopnum));
		i.putExtra("StopID", stopnum);
		i.putExtra("Destination", destination);
		
		if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
			i.putExtra("VehicleNumber", vehicleNumber);
		}

		if (route != null && !route.isEmpty()) {
			i.putExtra("Route", route);
		}
		
		context.stopService(i);
		context.startService(i);
		
		Tracker t = Tracking.getTracker(context);
		
		t.send(new HitBuilders.EventBuilder()
        	.setCategory("NotifyService")
        	.setAction("SetNotifyService")
        	.set( "route", route )
            .set( "stop", String.valueOf( stopnum ) )
            .set( "vehicle", vehicleNumber )
            .set( "destination", destination )
        	.build());
		
	}
    
    @Override
    protected void onStop() {
    	super.onStop();
    	PredictionManager.getInstance().pauseTracking();
    	populator.StopPopulating();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	populator.StartPopulating();
    	PredictionManager.getInstance().resumeTracking();
    }
    
    protected TextWatcher RouteTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
			String routeText  = routeField.getText().toString();
			
			populator.RouteSelectionChanged(routeText);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {		}
    };

    
    protected TextWatcher StopTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
			String stopText   = stopField.getText().toString();
			
			populator.StopSelectionChanged(stopText);
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3 ) {}
    };

}
