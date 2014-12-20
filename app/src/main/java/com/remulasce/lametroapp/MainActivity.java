package com.remulasce.lametroapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Logging;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.fragments.ServiceRequestFragment;
import com.remulasce.lametroapp.pred.PredictionManager;
import com.remulasce.lametroapp.pred.Trip;
import com.remulasce.lametroapp.static_data.StopNameSQLHelper;
import com.remulasce.lametroapp.types.Destination;
import com.remulasce.lametroapp.types.Route;
import com.remulasce.lametroapp.types.ServiceRequest;
import com.remulasce.lametroapp.types.Stop;
import com.remulasce.lametroapp.types.Vehicle;

public class MainActivity extends ActionBarActivity implements ServiceRequestFragment.OnServiceRequestListChanged {
    private static final String TAG = "TripListActivity";

//    Button setButton;
//    Button stopButton;
//    EditText stopField;
//    EditText routeField;
//    EditText vehicleField;
    EditText omniField;
    Button omniButton;

    ServiceRequestFragment requestFragment;

    ListView tripList;
    TripPopulator populator;
    StopNameSQLHelper stopNames;

    Tracker t;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        setupActionBar();
        startAnalytics();

        linkViewReferences();
        setupActionListeners();

        setupDefaults( getIntent() );
        initializeStaticData();
    }

    private void setupActionBar() {
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.omni_search_box, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    protected void linkViewReferences() {
        omniField = (EditText) findViewById( R.id.omni_text );
        omniButton = (Button) findViewById( R.id.omni_button );
//        stopField = (EditText) findViewById( R.id.idtext );
//        routeField = (EditText) findViewById( R.id.routetext );
//        vehicleField = (EditText) findViewById( R.id.vehicleNum );
//        setButton = (Button) findViewById( R.id.setbutton );
//        stopButton = (Button) findViewById( R.id.stopbutton );

        tripList = (ListView) findViewById( R.id.tripList );

        requestFragment = (ServiceRequestFragment) getFragmentManager()
                .findFragmentById(R.id.service_request_fragment);
    }

    protected void setupActionListeners() {

//        setButton.setOnClickListener( setButtonListener );
//        stopButton.setOnClickListener( stopButtonListener );
        omniButton.setOnClickListener( omniButtonListener );

//        stopField.addTextChangedListener( StopTextWatcher );
//        routeField.addTextChangedListener( RouteTextWatcher );
//        vehicleField.addTextChangedListener( VehicleTextWatcher );

        populator = new TripPopulator( tripList );

        tripList.setOnItemClickListener( tripClickListener );
    }

    private void initializeStaticData() {
        stopNames = new StopNameSQLHelper(this);
        stopNames.initialize();

        Log.d(TAG, "Test stopname for 30000: "+stopNames.getStopName("30000"));
        Log.d(TAG, "Test stopid for Patsaouras Transit Plaza: "+stopNames.getStopID("Patsaouras Transit Plaza"));
    }

    protected OnItemClickListener tripClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
            Trip trip = (Trip) parent.getItemAtPosition( position );
            trip.executeAction( MainActivity.this );
        }
    };

    private void startNotifyServiceFromViews() {
        /*
        Stop stop = new Stop( stopField.getText().toString() );
        Vehicle veh = new Vehicle( vehicleField.getText().toString() );
        Route route = new Route( routeField.getText().toString() );

        t.send( new HitBuilders.EventBuilder().setCategory( "NotifyService" )
                .setAction( "NotifyService Set Button" ).build() );

        SetNotifyService( stop, route, null, veh, MainActivity.this );
        */
    }

    protected OnClickListener setButtonListener = new OnClickListener() {
        public void onClick( View v ) {
            startNotifyServiceFromViews();
        }

    };

    protected OnClickListener omniButtonListener = new OnClickListener() {
        public void onClick( View v ) {
            //stopField.append( " "+omniField.getText());

            // TODO need something in here to check whether a stopid or stopname was entered
            String requestName = omniField.getText().toString();
            requestName = stopNames.getStopName(requestName);
            ServiceRequest serviceRequest = new ServiceRequest(requestName);
            requestFragment.AddServiceRequest(serviceRequest);

            omniField.getEditableText().clear();
            omniField.clearFocus();
        }

    };

    private void stopNotifyService() {
        Intent i = new Intent( MainActivity.this, ArrivalNotifyService.class );

        t.send( new HitBuilders.EventBuilder()
                .setCategory( "NotifyService" )
                .setAction( "NotifyService Stop Button" )
                .build() );

        MainActivity.this.stopService( i );
    }

    protected OnClickListener stopButtonListener = new OnClickListener() {
        public void onClick( View v ) {
            stopNotifyService();
        }

    };

    protected void startAnalytics() {
        t = Tracking.getTracker( getApplicationContext() );
    }

    protected void setupDefaults( Intent bundle ) {
        Route route = new Route( bundle.getStringExtra( "Route" ) );
        Stop stop = new Stop( bundle.getStringExtra( "StopID" ) );
        Vehicle veh = new Vehicle( bundle.getStringExtra( "VehicleNumber" ) );

        if ( route.isValid() ) {
//            routeField.setText( route.getString() );
        }
        if ( stop.isValid() ) {
//            stopField.setText( stop.getString() );
        }
        if ( veh.isValid() ) {
//            vehicleField.setText( veh.getString() );
        }

        boolean intentFilled = route.isValid() || stop.isValid() || veh.isValid();

        if ( !intentFilled ) {
//            routeField.setText( getPreferences(
//                                                MODE_PRIVATE ).getString( "routeField", "" ) );
//            stopField.setText( getPreferences(
//                                               MODE_PRIVATE ).getString( "stopField", "" ) );
//            vehicleField.setText( getPreferences(
//                                                  MODE_PRIVATE ).getString( "vehicleField", "" ) );
        }

        String label = ( intentFilled ) ? "Form Filled From Intent"
                : "Form Filled From Preferences";
        t.send( new HitBuilders.EventBuilder()
                .setCategory( "MainScreen" )
                .setAction( "Field Population" )
                .setLabel( label )
                .build() );

//        populator.StopSelectionChanged( stopField.getText().toString() );
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

//    protected TextWatcher RouteTextWatcher = new TextWatcher() {
//        @Override
//        public void afterTextChanged( Editable arg0 ) {
//            String routeText = routeField.getText().toString();
//
//            SharedPreferences.Editor e = getPreferences(
//                                                         MODE_PRIVATE ).edit();
//            e.putString( "routeField", routeText );
//            e.commit();
//
//            populator.RouteSelectionChanged( routeText );
//        }
//
//        @Override
//        public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//
//        @Override
//        public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//    };
//
//    protected TextWatcher StopTextWatcher = new TextWatcher() {
//        @Override
//        public void afterTextChanged( Editable arg0 ) {
//            String stopText = stopField.getText().toString();
//
//            SharedPreferences.Editor e = getPreferences(
//                                                         MODE_PRIVATE ).edit();
//            e.putString( "stopField", stopText );
//            e.commit();
//
//            long start = System.currentTimeMillis();
//
//            populator.StopSelectionChanged( stopText );
//
//            long spent = System.currentTimeMillis() - start;
//            Log.d( "UITiming", "AfterTextChanged return: " + spent );
//            t.send( new HitBuilders.TimingBuilder()
//                    .setCategory( "UI Delay" )
//                    .setValue( spent )
//                    .setVariable( "Trip List" )
//                    .setLabel( "Stop Text Changed" )
//                    .build() );
//        }
//
//        @Override
//        public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//
//        @Override
//        public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//    };
//
//    protected TextWatcher VehicleTextWatcher = new TextWatcher() {
//        @Override
//        public void afterTextChanged( Editable arg0 ) {
//            String vehicleText = vehicleField.getText().toString();
//            SharedPreferences.Editor e = getPreferences(
//                                                         MODE_PRIVATE ).edit();
//            e.putString( "vehicleField", vehicleText );
//            e.commit();
//
//        }
//
//        @Override
//        public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//
//        @Override
//        public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {}
//    };

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        menu.add( "Clear Fields" );
        menu.add( "Stop Arrival Notification" );
        menu.add( "Start Arrival Notification" );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if ( item.getTitle().equals( "Start Arrival Notification" ) ) {
            startNotifyServiceFromViews();
        } else if ( item.getTitle().equals( "Stop Arrival Notification" ) ) {
            stopNotifyService();
        } else if ( item.getTitle().equals( "Clear Fields" ) ) {
//            stopField.setText( "" );
//            routeField.setText( "" );
//            vehicleField.setText( "" );
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO implement
    }

    @Override
    public TripPopulator getTripPopulator() {
        return populator;
    }
}
