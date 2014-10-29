package com.remulasce.lametroapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

	Button setButton;
	Button stopButton;
	EditText stopField;
	EditText routeField;
	
	ListView tripList;
	TripPopulator populator;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopField = (EditText) findViewById(R.id.idtext);
        routeField = (EditText) findViewById(R.id.routetext);
        setButton = (Button) findViewById(R.id.setbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        
        tripList = (ListView) findViewById(R.id.tripList);
        
        setButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View v) {
        		//Start service
        		String stopText = stopField.getText().toString();
        		int stopnum = Integer.valueOf(stopText);
        		String route = routeField.getText().toString();
        		
        		Intent i = new Intent(MainActivity.this, ArrivalNotifyService.class);

        		i.putExtra("Agency", LaMetroUtil.getAgencyFromRoute(route, stopnum));
        		i.putExtra("StopID", stopnum);

        		if (route != null && !route.isEmpty()) {
        			i.putExtra("Route", route);
        		}
        		
        		MainActivity.this.stopService(i);
        		MainActivity.this.startService(i);
        	}
        });
       
        stopButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View v) {
        		Intent i = new Intent(MainActivity.this, ArrivalNotifyService.class);
        		
        		MainActivity.this.stopService(i);
        	}
        });
        
        stopField.addTextChangedListener(StopTextWatcher);
        routeField.addTextChangedListener(RouteTextWatcher);

        populator = new TripPopulator( tripList );
        populator.StartPopulating();
        
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
