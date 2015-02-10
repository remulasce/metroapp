package com.remulasce.lametroapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;
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
//    ImageButton omniButton;
    Button clearButton;
    Button donateButton;

    OmniBarInputHandler omniHandler;
    ServiceRequestListFragment requestFragment;

    ListView tripList;
    TextView tripListHint;
    View networkStatusView;
    ProgressBar tripListProgress;

    TripPopulator populator;
    MetroStaticsProvider staticsProvider;
    OmniAutoCompleteAdapter autoCompleteAdapter;
    MetroLocationRetriever locationService;
    SettingFieldSaver fieldSaver;
    NetworkStatusReporter networkStatusReporter;

    Tracker t;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

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
        setupNetworkStatus();
        setupAboutPage();

        setupDefaults( getIntent() );
    }

    private void setupNetworkStatus() {
        networkStatusReporter = new NetworkStatusReporter(networkStatusView);
        PredictionManager.setStatusReporter(networkStatusReporter);
    }

    private void setupFieldSaver() {
        fieldSaver = new SettingFieldSaver(this, staticsProvider);
    }

    private void setupLocation() {
        locationService = new MetroLocationRetriever(this, staticsProvider);
        GlobalLocationProvider.setRetriever(locationService);
    }

    private void setupOmniBar() {
        autoCompleteAdapter = new OmniAutoCompleteAdapter(this, R.layout.omnibar_dropdown_item, R.id.item, staticsProvider, locationService);
        omniField.setAdapter(autoCompleteAdapter);
        omniField.setThreshold(3);

        omniHandler = new OmniBarInputHandler(omniField, null, clearButton, requestFragment, staticsProvider, staticsProvider, t, this);
    }

    private void setupActionBar() {
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setCustomView(R.layout.omni_search_box);
        mActionBar.setDisplayShowCustomEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                t.send(new HitBuilders.EventBuilder()
                        .setCategory("About Page")
                        .setAction("Pane Opened")
                        .build());

                t.setScreenName("About Page");
                t.send(new HitBuilders.AppViewBuilder().build());
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    protected void setupAboutPage() {
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.send( new HitBuilders.EventBuilder()
                        .setCategory( "Monetization" )
                        .setAction( "Donate Opened" )
                        .setLabel( "About Page Button" )
                        .build() );
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=85JRNL5K6T7XE&lc=US&item_name=LA%20Metro%20Companion%20%7c%20Fintan%20O%27Grady&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
                startActivity(browserIntent);
            }
        });
    }

    protected void linkViewReferences() {
        omniField = (AutoCompleteTextView) findViewById( R.id.omni_text );
//        omniButton = (ImageButton) findViewById( R.id.omni_button );
        clearButton = (Button) findViewById( R.id.omni_clear_button );
        donateButton = (Button) findViewById( R.id.donate_button );

        tripList = (ListView) findViewById( R.id.tripList );
        tripListHint = (TextView) findViewById( R.id.trip_list_hint );
        tripListProgress = (ProgressBar) findViewById(R.id.trip_list_progress);
        networkStatusView = (View) findViewById(R.id.network_status_bar);

        requestFragment = (ServiceRequestListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.service_request_fragment);
    }

    protected void setupActionListeners() {
        populator = new TripPopulator( tripList, tripListHint, tripListProgress, this );
        tripList.setOnItemClickListener( tripClickListener );
    }

    private void initializeStaticData() {
        staticsProvider = new MetroStaticsProvider(this);
        staticsProvider.initialize();

        // ugh.
        LaMetroUtil.locationTranslator = staticsProvider;
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
        Stop add = new Stop(stopID);
        add.setLocation(staticsProvider.getStopLocation(add));

        ServiceRequest serviceRequest = new StopServiceRequest(add, displayName);

        if (serviceRequest.isValid()) {
            requestFragment.AddServiceRequest(serviceRequest);
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
        }
    }

    protected void startAnalytics() {

        t = Tracking.getTracker( getApplicationContext() );
        t.setScreenName("About Page");
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    protected void setupDefaults( Intent bundle ) {
        Route route = new Route( bundle.getStringExtra( "Route" ) );
        Stop stop = new Stop( bundle.getStringExtra( "StopID" ) );
        Vehicle veh = new Vehicle( bundle.getStringExtra( "VehicleNumber" ) );

        if ( stop.isValid() ) {
//            makeServiceRequest(stop.getStopID());
            // Early devices don't support notification actions
            // So this is the only way to disable arrival notification for them
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                NotifyServiceManager.stopNotifyService(this);
            }
        }

        // It's confusing to clear stuff out when you hit the notification.
        boolean intentFilled = false; // route.isValid() || stop.isValid() || veh.isValid();

        if ( !intentFilled ) {
            requestFragment.loadSavedRequests();
        }

        String label = "No Form Prefill";
        if ( intentFilled ) { label = "Form Filled From Intent"; }
        else if (requestFragment.numRequests() > 0) { label = "Form Filled From Preferences"; }

        t.send( new HitBuilders.EventBuilder()
                .setCategory( "MainScreen" )
                .setAction( "Field Population" )
                .setLabel( label )
                .build() );
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStart() {
        super.onStart();
        populator.StartPopulating();
        PredictionManager.getInstance().resumeTracking();
//        Logging.StartSavingLogcat(this);
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
//        Logging.StopSavingLogcat();
    }

    @Override
    protected void onResume() {
        super.onResume();

        t.setScreenName("Main Screen");
        t.send(new HitBuilders.AppViewBuilder().build());

        locationService.startLocating(this);

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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
