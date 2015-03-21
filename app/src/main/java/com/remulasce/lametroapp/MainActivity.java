package com.remulasce.lametroapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.remulasce.lametroapp.analytics.AndroidLog;
import com.remulasce.lametroapp.analytics.AndroidTracking;
import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.ServiceRequestHandler;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.location.GlobalLocationProvider;
import com.remulasce.lametroapp.components.location.MetroLocationRetriever;
import com.remulasce.lametroapp.components.network_status.AndroidNetworkStatusReporter;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.omni_bar.OmniBarInputHandler;
import com.remulasce.lametroapp.components.omni_bar.ProgressAutoCompleteTextView;
import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.components.persistence.SerializedFileFieldSaver;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.platform_support.AndroidApacheHTTP;
import com.remulasce.lametroapp.static_data.HardcodedMetroColors;
import com.remulasce.lametroapp.static_data.MetroStaticsProvider;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;

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

    private ServiceRequestHandler requestHandler;
    private HTTPGetter network;
    private MetroStaticsProvider staticsProvider;
    private OmniAutoCompleteAdapter autoCompleteAdapter;
    private MetroLocationRetriever locationService;
    private RouteColorer routeColorer;
    private SerializedFileFieldSaver fieldSaver;
    private NetworkStatusReporter networkStatusReporter;

    private TripPopulator tripPopulator;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        startAnalytics();
        initializeStaticData();
        initializeDynamicData();
        setupLocation();

        setupActionBar();
        linkViewReferences();

        setupActionListeners();
        setupOmniBar();
        setupNetworkStatus();
        setupAboutPage();

        setupDefaults( getIntent() );
    }

    private void initializeDynamicData() {
        network = new AndroidApacheHTTP();
        HTTPGetter.setHTTPGetter(network);
    }

    private void setupNetworkStatus() {
        networkStatusReporter = new AndroidNetworkStatusReporter(networkStatusView);
        PredictionManager.setStatusReporter(networkStatusReporter);
    }

    private void setupFieldSaver() {
        fieldSaver = new SerializedFileFieldSaver(this);
    }

    private void setupLocation() {
        locationService = new MetroLocationRetriever(this, staticsProvider);
        GlobalLocationProvider.setRetriever(locationService);
    }

    private void setupOmniBar() {
        autoCompleteAdapter = new OmniAutoCompleteAdapter(this, R.layout.omnibar_dropdown_item, R.id.item, staticsProvider, locationService);
        omniField.setAdapter(autoCompleteAdapter);
        omniField.setThreshold(0);

        omniHandler = new OmniBarInputHandler(omniField, null, clearButton, autocompleteProgress, requestFragment, staticsProvider, staticsProvider, staticsProvider, null, this);
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
                Tracking.setScreenName("About Page");
                Tracking.sendEvent("About Page", "Pane Opened");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    void setupAboutPage() {
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracking.sendEvent("Monetization", "Donate Opened", "About Page Button");

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
        requestHandler = new ServiceRequestHandler();
        tripPopulator = new TripPopulator( requestHandler, tripList, tripListHint, tripListProgress, this );

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

    void startAnalytics() {
        AndroidTracking tracking = new AndroidTracking(this);
        Tracking.setTracker(tracking);

        Log log = new AndroidLog();
        Log.SetLogger(log);
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

        Tracking.sendEvent("MainScreen", "Field Population", label);
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
        requestHandler.StartPopulating();
        tripPopulator.StartPopulating();
        PredictionManager.getInstance().resumeTracking();
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
        requestHandler.StopPopulating();
        tripPopulator.StopPopulating();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Tracking.setScreenName("Main Screen");
        locationService.startLocating(this);

    }

    // Ugly hack to show history suggestions as soon as ap loads
    // Except, Android won't actually tell you when it's ok with dialogs showing
    // So instead we check every xms until we actually have a window.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            Handler h = new Handler(Looper.getMainLooper());
            h.postDelayed(showDropdownOnStart, 100);
        }
    }

    Runnable showDropdownOnStart = new Runnable() {
        @Override
        public void run() {
            if (omniField.getWindowVisibility() != View.GONE) {// && omniField.isFocused()) {
                Log.i(TAG, "Showing omni dropdown after startup");
                omniField.requestFocus();
                omniField.showDropDown();
            } else {
                Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(showDropdownOnStart, 100);
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (omniField.isFocused()) {
                Rect outRect = new Rect();
                omniField.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    omniField.clearFocus();
                    //
                    // Hide keyboard
                    //
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
    public ServiceRequestHandler getTripPopulator() {
        return requestHandler;
    }

    @Override
    public FieldSaver getFieldSaver() {
        if (fieldSaver == null) {
            setupFieldSaver();
        }
        return fieldSaver;
    }
}
