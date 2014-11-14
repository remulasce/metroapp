package com.remulasce.lametroapp;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.pred.PredictionManager;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.analytics.Tracking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

	Button setButton;
	Button stopButton;
	EditText stopField;
	EditText routeField;
	EditText vehicleField;
	
	ListView tripList;
	TripPopulator populator;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopField = (EditText) findViewById(R.id.idtext);
        routeField = (EditText) findViewById(R.id.routetext);
        vehicleField = (EditText) findViewById(R.id.vehicleNum);
        setButton = (Button) findViewById(R.id.setbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        
        tripList = (ListView) findViewById(R.id.tripList);
        
        setButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View v) {
        		//Start service
        		String stopText = stopField.getText().toString();
        		int stopnum = Integer.valueOf(stopText);
        		String vehicleText = vehicleField.getText().toString();
        		String route = routeField.getText().toString();
        		
        		SetNotifyService(stopnum, route, null, vehicleText, MainActivity.this);
        	}
        });
       
        stopButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View v) {
        		Intent i = new Intent(MainActivity.this, ArrivalNotifyService.class);
        		
        		Tracker t = Tracking.getTracker(MainActivity.this);
        		
        		t.setScreenName("StopNotify");
        		t.send( new HitBuilders.AppViewBuilder().build());
        		
        		t.send(new HitBuilders.EventBuilder()
                	.setCategory("category")
                	.setAction("NotifyService Stop")
                	.setLabel("Label")
                	.build());
        		
        		MainActivity.this.stopService(i);
        	}
        });
        
        stopField.addTextChangedListener(StopTextWatcher);
        routeField.addTextChangedListener(RouteTextWatcher);

        populator = new TripPopulator( tripList );
        
        populator.StopSelectionChanged(stopField.getText().toString());
        
        tripList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Trip t = (Trip)parent.getItemAtPosition(position);
				t.executeAction(MainActivity.this);
			}
        });
        
        
        unpackExtras(getIntent());
        
        Tracking.getTracker(getApplicationContext()).enableAdvertisingIdCollection(true);
        Tracking.getTracker(getApplicationContext()).enableAutoActivityTracking(true);
        Tracking.getTracker(getApplicationContext()).enableExceptionReporting(true);
    }
    
    protected void unpackExtras(Intent bundle) {
    	
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
		
		t.setScreenName("SetNotify");
		t.send( new HitBuilders.AppViewBuilder().build());
		
		t.send(new HitBuilders.EventBuilder()
        	.setCategory("category")
        	.setAction("NotifyService Start")
        	.setLabel("Label")
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
				int arg3) {		}
    };
    
    
    
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
