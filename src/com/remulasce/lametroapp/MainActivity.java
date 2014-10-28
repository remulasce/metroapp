package com.remulasce.lametroapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity {

	Button setButton;
	Button stopButton;
	EditText idField;
	EditText routeField;
	Spinner agencySpinner;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        agencySpinner = (Spinner) findViewById(R.id.agencyspinner);
        idField = (EditText) findViewById(R.id.idtext);
        routeField = (EditText) findViewById(R.id.routetext);
        setButton = (Button) findViewById(R.id.setbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        
        setButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View v) {
        		//Start service
        		String stopText = idField.getText().toString();
        		int stopnum = Integer.valueOf(stopText);
        		String route = routeField.getText().toString();
        		
        		Intent i = new Intent(MainActivity.this, ArrivalNotifyService.class);
        		i.putExtra("StopID", stopnum);
        		i.putExtra("Agency", String.valueOf(agencySpinner.getSelectedItem()));
        		if (route != null && !route.isEmpty()) {
        			i.putExtra("Route", routeField.getText().toString());
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
        
    }


    
    
    
    
    
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
