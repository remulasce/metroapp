package com.remulasce.lametroapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Logging;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.components.FieldSaver;
import com.remulasce.lametroapp.components.LocationRetriever;
import com.remulasce.lametroapp.components.MetroLocationRetriever;
import com.remulasce.lametroapp.components.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.ServiceRequestFragment;
import com.remulasce.lametroapp.components.SettingFieldSaver;
import com.remulasce.lametroapp.pred.PredictionManager;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.static_data.StopNameSQLHelper;
import com.remulasce.lametroapp.types.Destination;
import com.remulasce.lametroapp.types.Route;
import com.remulasce.lametroapp.types.ServiceRequest;
import com.remulasce.lametroapp.types.Stop;
import com.remulasce.lametroapp.types.Vehicle;

import java.util.Collection;

public class MainActivity extends ActionBarActivity implements ServiceRequestFragment.OnServiceRequestListChanged {
    private static final String TAG = "TripListActivity";

    AutoCompleteTextView omniField;
    Button omniButton;

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
        setupActionBar();
        startAnalytics();

        initializeStaticData();

        linkViewReferences();
        setupActionListeners();
        setupLocation();
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
        omniButton.setOnClickListener( omniButtonListener );
        omniField.setOnEditorActionListener( omniDoneListener );

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

    // This is an extremely low level check. The ServiceRequest itself will have a better
    // idea whether it can actually track anything.
    private boolean isOmniInputValid(String input) {
        return input != null && !input.isEmpty();

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

    protected TextView.OnEditorActionListener omniDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

            String requestText = omniField.getText().toString();
            makeServiceRequestFromOmniInput(requestText);

            return true;
        }
    };
    protected OnClickListener omniButtonListener = new OnClickListener() {
        public void onClick( View v ) {
            String requestText = omniField.getText().toString();
            makeServiceRequestFromOmniInput(requestText);
        }

    };

    // Parses the input to figure out if it's a stopid, stopname, etc.
    private void makeServiceRequestFromOmniInput(String requestText) {
        if (isOmniInputValid(requestText)) {
            try { // No really, this should never crash the app.
                // Need to check which way to convert- stopname to stopid, or vice-versa
                String convertedName = stopNames.getStopName(requestText);
                Collection<String> convertedID = stopNames.getStopID(requestText);

                // It was a valid StopID
                if (convertedName != null) {
                    makeServiceRequest(requestText, convertedName);
                    omniField.getEditableText().clear();
                    omniField.clearFocus();

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("AutoComplete")
                            .setAction("AutoComplete Add Button")
                            .setLabel("StopID")
                            .build());
                }
                // It was a valid stop name
                else if (convertedID != null && !convertedID.isEmpty()) {
                    for (String id : convertedID)
                        makeServiceRequest(id, requestText);
                    omniField.getEditableText().clear();
                    omniField.clearFocus();

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("AutoComplete")
                            .setAction("AutoComplete Add Button")
                            .setLabel("StopName")
                            .build());
                }
                // Not valid.
                else {
                    Log.i(TAG, "Couldn't parse omnibox input into id or stopname, ignoring");
                    Toast.makeText(this, "Invalid stopname or id", Toast.LENGTH_SHORT).show();

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("AutoComplete")
                            .setAction("AutoComplete Add Button")
                            .setLabel("Invalid")
                            .build());
                }
            } catch (Exception e) {
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("AutoComplete")
                        .setAction("AutoComplete Add Button")
                        .setLabel("Exception")
                        .build());
            }
        }
    }

    private void stopNotifyService() {
        Intent i = new Intent( MainActivity.this, ArrivalNotifyService.class );

        t.send( new HitBuilders.EventBuilder()
                .setCategory( "NotifyService" )
                .setAction( "NotifyService Stop Button" )
                .build() );

        MainActivity.this.stopService( i );
    }

    protected void startAnalytics() {
        t = Tracking.getTracker( getApplicationContext() );
    }

    protected void setupDefaults( Intent bundle ) {
        Route route = new Route( bundle.getStringExtra( "Route" ) );
        Stop stop = new Stop( bundle.getStringExtra( "StopID" ) );
        Vehicle veh = new Vehicle( bundle.getStringExtra( "VehicleNumber" ) );

        if ( stop.isValid() ) {
             makeServiceRequestFromOmniInput(stop.getStopID());
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

    public static void SetNotifyService( Stop stop, Route route, Destination destination,
            Vehicle vehicle, Context context ) {

        Tracker t = Tracking.getTracker( context );
        Intent i = new Intent( context, ArrivalNotifyService.class );

        if ( !stop.isValid() ) {
            t.send( new HitBuilders.EventBuilder()
                    .setCategory( "NotifyService" )
                    .setAction( "SetNotifyService" )
                    .setLabel( "Stop invalid" ).build() );
            return;
        }

        try {
            i.putExtra( "Agency", LaMetroUtil.getAgencyFromRoute( route, stop ) );
            i.putExtra( "StopID", stop.getNum() );

            if ( destination != null && destination.isValid() ) {
                i.putExtra( "Destination", destination.getString() );
            }
            if ( vehicle != null && vehicle.isValid() ) {
                i.putExtra( "VehicleNumber", vehicle.getString() );
            }
            if ( route != null && route.isValid() ) {
                i.putExtra( "Route", route.getString() );
            }

            context.stopService( i );
            context.startService( i );

            t.send( new HitBuilders.EventBuilder().setCategory( "NotifyService" )
                    .setAction( "SetNotifyService" ).build() );
        } catch ( IllegalArgumentException e ) {
            t.send( new HitBuilders.EventBuilder().setCategory( "NotifyService" )
                    .setAction( "Bad input to notify service" ).build() );
        }
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
            stopNotifyService();
        } else if ( item.getTitle().equals( "Clear Fields" ) ) {
            clearFields();
        }

        return true;
    }

    private void clearFields() {
        omniField.setText("");
        t.send( new HitBuilders.EventBuilder()
                .setCategory( "" )
                .setAction( "AutoComplete Add Button" )
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
