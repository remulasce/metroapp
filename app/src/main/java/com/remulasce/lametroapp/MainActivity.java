package com.remulasce.lametroapp;

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
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Logging;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.components.location.MetroLocationRetriever;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.omni_bar.OmniBarInputHandler;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.components.persistence.SettingFieldSaver;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.static_data.MetroStaticsProvider;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.Vehicle;

public class MainActivity extends ActionBarActivity implements ServiceRequestListFragment.ServiceRequestListFragmentSupport {
    private static final String TAG = "MainActivity";

    AutoCompleteTextView omniField;
    ImageButton omniButton;
    Button clearButton;

    OmniBarInputHandler omniHandler;
    ServiceRequestListFragment requestFragment;

    ListView tripList;

    TripPopulator populator;
    MetroStaticsProvider staticsProvider;
    OmniAutoCompleteAdapter autoCompleteAdapter;
    MetroLocationRetriever locationService;
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
        locationService = new MetroLocationRetriever(this, staticsProvider);
    }

    private void setupOmniBar() {
        autoCompleteAdapter = new OmniAutoCompleteAdapter(this, R.layout.omnibar_dropdown_item, R.id.item, staticsProvider, locationService);
        omniField.setAdapter(autoCompleteAdapter);
        omniField.setThreshold(3);

        omniHandler = new OmniBarInputHandler(omniField, omniButton, clearButton, requestFragment, staticsProvider, t, this);
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
        omniButton = (ImageButton) findViewById( R.id.omni_button );
        clearButton = (Button) findViewById( R.id.omni_clear_button );

        tripList = (ListView) findViewById( R.id.tripList );

        requestFragment = (ServiceRequestListFragment) getFragmentManager()
                .findFragmentById(R.id.service_request_fragment);
    }

    protected void setupActionListeners() {
        populator = new TripPopulator( tripList );
        tripList.setOnItemClickListener( tripClickListener );
    }

    private void initializeStaticData() {
        staticsProvider = new MetroStaticsProvider(this);
        staticsProvider.initialize();
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

        displayName = staticsProvider.getStopName(stopID);
        if (displayName == null) {
            displayName = stopID;
        }

        makeServiceRequest(stopID, displayName);
    }
    private void makeServiceRequest( String stopID, String displayName ) {
        Log.d(TAG, "Making service request from stopID: "+stopID+", display: "+displayName);
        ServiceRequest serviceRequest = new ServiceRequest(stopID);
//        serviceRequest.setDisplayName(displayName+ ", "+stopID);
        serviceRequest.setDisplayName(displayName);

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
    protected void onPause() {
        super.onPause();
        locationService.stopLocating(this);
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
        locationService.startLocating(this);
        PredictionManager.getInstance().resumeTracking();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        return true;
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
