package com.remulasce.lametroapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Logging;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.FieldSaver;
import com.remulasce.lametroapp.components.LocationRetriever;
import com.remulasce.lametroapp.components.MetroLocationRetriever;
import com.remulasce.lametroapp.components.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.OmniBarInputHandler;
import com.remulasce.lametroapp.components.ServiceRequestFragment;
import com.remulasce.lametroapp.components.SettingFieldSaver;
import com.remulasce.lametroapp.pred.PredictionManager;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.static_data.StopNameSQLHelper;
import com.remulasce.lametroapp.types.Route;
import com.remulasce.lametroapp.types.ServiceRequest;
import com.remulasce.lametroapp.types.Stop;
import com.remulasce.lametroapp.types.Vehicle;

public class MainActivity extends ActionBarActivity implements ServiceRequestFragment.OnServiceRequestListChanged {
    private static final String TAG = "MainActivity";

    AutoCompleteTextView omniField;
    Button omniButton;

    OmniBarInputHandler omniHandler;
    ServiceRequestFragment requestFragment;

    ListView tripList;

    TripPopulator populator;
    StopNameSQLHelper stopNames;
    OmniAutoCompleteAdapter autoCompleteAdapter;
    LocationRetriever locationService;
    SettingFieldSaver fieldSaver;

    Tracker t;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        startAnalytics();
        initializeStaticData();
        setupLocation();

        setupActionBar();
        linkViewReferences();

        setupActionListeners();
        setupOmniBar();

        setupDefaults( getIntent() );
    }

    private void setupFieldSaver() {
        fieldSaver = new SettingFieldSaver(this);
    }

    private void setupLocation() {
        locationService = new MetroLocationRetriever(this, stopNames);
    }

    private void setupOmniBar() {
        autoCompleteAdapter = new OmniAutoCompleteAdapter(this, R.layout.omnibar_dropdown_item, R.id.item, stopNames, locationService);
        omniField.setAdapter(autoCompleteAdapter);
        omniField.setThreshold(3);

        omniHandler = new OmniBarInputHandler(omniField, omniButton, requestFragment, stopNames, t, this);
    }

    private void setupActionBar() {
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setCustomView(R.layout.omni_search_box);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    protected void linkViewReferences() {
        omniField = (AutoCompleteTextView) findViewById( R.id.omni_text );
        omniButton = (Button) findViewById( R.id.omni_button );

        tripList = (ListView) findViewById( R.id.tripList );

        requestFragment = (ServiceRequestFragment) getFragmentManager()
                .findFragmentById(R.id.service_request_fragment);
    }

    protected void setupActionListeners() {
        populator = new TripPopulator( tripList );
        tripList.setOnItemClickListener( tripClickListener );
    }

    private void initializeStaticData() {
        stopNames = new StopNameSQLHelper(this);
        stopNames.initialize();
    }

    protected OnItemClickListener tripClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
            Trip trip = (Trip) parent.getItemAtPosition( position );
            trip.executeAction( MainActivity.this );
        }
    };

    // Try to find the stopname from the table
    private void makeServiceRequest( String stopID ) {
        String displayName;

        displayName = stopNames.getStopName(stopID);
        if (displayName == null) {
            displayName = stopID;
        }

        makeServiceRequest(stopID, displayName);
    }
    private void makeServiceRequest( String stopID, String displayName ) {
        Log.d(TAG, "Making service request from stopID: "+stopID+", display: "+displayName);
        ServiceRequest serviceRequest = new ServiceRequest(stopID);
        serviceRequest.setDisplayName(displayName+ ", "+stopID);

        if (serviceRequest.isValid()) {
            requestFragment.AddServiceRequest(serviceRequest);
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
        }
    }

    protected void startAnalytics() {
        t = Tracking.getTracker( getApplicationContext() );
    }

    protected void setupDefaults( Intent bundle ) {
        Route route = new Route( bundle.getStringExtra( "Route" ) );
        Stop stop = new Stop( bundle.getStringExtra( "StopID" ) );
        Vehicle veh = new Vehicle( bundle.getStringExtra( "VehicleNumber" ) );

        if ( stop.isValid() ) {
            makeServiceRequest(stop.getStopID());
        }

        boolean intentFilled = route.isValid() || stop.isValid() || veh.isValid();

        if ( !intentFilled ) {
            requestFragment.loadSavedRequests();
        }

        String label = ( intentFilled ) ? "Form Filled From Intent"
                : "Form Filled From Preferences";
        t.send( new HitBuilders.EventBuilder()
                .setCategory( "MainScreen" )
                .setAction( "Field Population" )
                .setLabel( label )
                .build() );
    }
    @Override
    protected void onStart() {
        super.onStart();
        Logging.StartSavingLogcat(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PredictionManager.getInstance().pauseTracking();
        populator.StopPopulating();
        Logging.StopSavingLogcat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populator.StartPopulating();
        PredictionManager.getInstance().resumeTracking();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        menu.add( "Clear Fields" );
        menu.add( "Stop Arrival Notification" );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if ( item.getTitle().equals( "Stop Arrival Notification" ) ) {
            NotifyServiceManager.stopNotifyService( this );
        } else if ( item.getTitle().equals( "Clear Fields" ) ) {
            clearFields();
        }

        return true;
    }

    private void clearFields() {
        omniField.setText("");
        t.send( new HitBuilders.EventBuilder()
                .setCategory( "MainScreen" )
                .setAction( "Clear Fields" )
                .build() );
    }

    @Override
    public TripPopulator getTripPopulator() {
        return populator;
    }

    @Override
    public FieldSaver getFieldSaver() {
        if (fieldSaver == null) {
            setupFieldSaver();
        }
        return fieldSaver;
    }
}
