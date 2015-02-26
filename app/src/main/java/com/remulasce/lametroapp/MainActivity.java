package com.remulasce.lametroapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.components.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.location.MetroLocationRetriever;
import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.omni_bar.OmniBarInputHandler;
import com.remulasce.lametroapp.components.omni_bar.ProgressAutoCompleteTextView;
import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.components.persistence.SerializedFileFieldSaver;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.dynamic_data.types.Trip;
import com.remulasce.lametroapp.static_data.HardcodedMetroColors;
import com.remulasce.lametroapp.static_data.MetroStaticsProvider;
import com.remulasce.lametroapp.static_data.RouteColorer;
import com.remulasce.lametroapp.static_data.types.RouteColor;

public class MainActivity extends ActionBarActivity implements ServiceRequestListFragment.ServiceRequestListFragmentSupport {
    private static final String TAG = "MainActivity";

    private ProgressAutoCompleteTextView omniField;
    private Button clearButton;
    private Button donateButton;
    private Button legalButton;
    private ProgressBar autocompleteProgress;

    private OmniBarInputHandler omniHandler;
    private ServiceRequestListFragment requestFragment;

    private ListView tripList;
    private TextView tripListHint;
    private View networkStatusView;
    private ProgressBar tripListProgress;

    private TripPopulator populator;
    private MetroStaticsProvider staticsProvider;
    private OmniAutoCompleteAdapter autoCompleteAdapter;
    private MetroLocationRetriever locationService;
    private RouteColorer routeColorer;
    private SerializedFileFieldSaver fieldSaver;
    private NetworkStatusReporter networkStatusReporter;

    private Tracker t;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

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
        fieldSaver = new SerializedFileFieldSaver(this, staticsProvider);
    }

    private void setupLocation() {
        locationService = new MetroLocationRetriever(this, staticsProvider);
        GlobalLocationProvider.setRetriever(locationService);
    }

    private void setupOmniBar() {
        autoCompleteAdapter = new OmniAutoCompleteAdapter(this, R.layout.omnibar_dropdown_item, R.id.item, staticsProvider, locationService);
        omniField.setAdapter(autoCompleteAdapter);
        omniField.setThreshold(3);

        omniHandler = new OmniBarInputHandler(omniField, null, clearButton, autocompleteProgress, requestFragment, staticsProvider, staticsProvider, t, this);
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

    void setupAboutPage() {
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

    void linkViewReferences() {
        omniField = (ProgressAutoCompleteTextView) findViewById( R.id.omni_text );
        clearButton = (Button) findViewById( R.id.omni_clear_button );
        donateButton = (Button) findViewById( R.id.donate_button );
        legalButton = (Button) findViewById(R.id.legal_info_button);
        autocompleteProgress = (ProgressBar) findViewById(R.id.autocomplete_progress);

        tripList = (ListView) findViewById( R.id.tripList );
        tripListHint = (TextView) findViewById( R.id.trip_list_hint );
        tripListProgress = (ProgressBar) findViewById(R.id.trip_list_progress);
        networkStatusView = findViewById(R.id.network_status_bar);

        requestFragment = (ServiceRequestListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.service_request_fragment);
    }

    void setupActionListeners() {
        populator = new TripPopulator( tripList, tripListHint, tripListProgress, this );
        tripList.setOnItemClickListener( tripClickListener );

        legalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.legal_info_dialog_title))
                        .setView(View.inflate(MainActivity.this, R.layout.legal_page, null)).show();
            }
        });
    }

    private void initializeStaticData() {
        staticsProvider = new MetroStaticsProvider(this);
        staticsProvider.initialize();

        routeColorer = new HardcodedMetroColors();

        // ugh.
        LaMetroUtil.locationTranslator = staticsProvider;
        LaMetroUtil.routeColorer = routeColorer;
    }

    private final OnItemClickListener tripClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {
            Trip trip = (Trip) parent.getItemAtPosition( position );
            trip.executeAction( MainActivity.this );
        }
    };

    void startAnalytics() {

        t = Tracking.getTracker( getApplicationContext() );
        t.setScreenName("About Page");
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    void setupDefaults(Intent bundle) {
        Stop stop = new Stop( bundle.getStringExtra( "StopID" ) );

        if ( stop.isValid() ) {
            // Early devices don't support notification actions
            // So this is the only way to disable arrival notification for them
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                NotifyServiceManager.stopNotifyService(this);
            }
        }

        requestFragment.loadSavedRequests();


        String label = "Form Filled From Preferences";

        if (requestFragment.numRequests() > 0) { label = "Form Filled From Preferences"; }

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
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

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
