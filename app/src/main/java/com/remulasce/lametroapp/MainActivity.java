package com.remulasce.lametroapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.remulasce.lametroapp.analytics.AndroidLog;
import com.remulasce.lametroapp.analytics.AndroidTracking;
import com.remulasce.lametroapp.components.location.CachedLocationRetriever;
import com.remulasce.lametroapp.components.network_status.AndroidNetworkStatusReporter;
import com.remulasce.lametroapp.components.omni_bar.OmniAutoCompleteAdapter;
import com.remulasce.lametroapp.components.omni_bar.OmniBarInputHandler;
import com.remulasce.lametroapp.components.omni_bar.ProgressAutoCompleteTextView;
import com.remulasce.lametroapp.components.omni_bar.UserStateProvider;
import com.remulasce.lametroapp.components.persistence.FieldSaver;
import com.remulasce.lametroapp.components.persistence.SerializedFileFieldSaver;
import com.remulasce.lametroapp.components.regions.RegionSettingsDialogFragment;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.components.tutorial.AndroidTutorialManager;
import com.remulasce.lametroapp.components.tutorial.TutorialManager;
import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.ServiceRequestHandler;
import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.java_core.location.GlobalLocationProvider;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.platform_support.AndroidApacheHTTP;
import com.remulasce.lametroapp.static_data.InstalledAgencyChecker;
import com.remulasce.lametroapp.static_data.MetroStaticsProvider;
import com.remulasce.lametroapp.static_data.hardcoded_hacks.HardcodedRouteColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements ServiceRequestListFragment.ServiceRequestListFragmentSupport {
  private static final String TAG = "MainActivity";
  public static final int PERMISSIONS_REQUEST_CODE = 5558999;

  private ProgressAutoCompleteTextView omniField;
  private Button clearButton;
  private Button legalButton;
  private Button settingsButton;
  private TextView aboutPaneHint;
  private TextView versionNumber;
  private ProgressBar autocompleteProgress;

  private OmniBarInputHandler omniHandler;
  private ServiceRequestListFragment requestFragment;
  private AndroidTutorialManager tutorialManager;

  private ListView tripList;
  private TextView tripListHint;
  private TextView tripListSecondaryHint;
  private View networkStatusView;
  private ProgressBar tripListProgress;

  private ServiceRequestHandler requestHandler;
  private HTTPGetter network;
  private MetroStaticsProvider staticsProvider;
  private OmniAutoCompleteAdapter autoCompleteAdapter;
  private CachedLocationRetriever locationService;
  private RouteColorer routeColorer;
  private SerializedFileFieldSaver fieldSaver;
  private NetworkStatusReporter networkStatusReporter;

  private TripPopulator tripPopulator;

  private DrawerLayout mDrawerLayout;
  private InstalledAgencyChecker installedAgencyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    startAnalytics();

    boolean missingPermissions = checkPermissionsNeeded();

    setupRegionalization();
    initializeStaticData();
    initializeDynamicData();
    setupLocation();

    setupActionBar();
    linkViewReferences();

    setupActionListeners();
    setupOmniBar();
    setupNetworkStatus();
    setupAboutPage();
    setupTutorials();

    setupDefaults(getIntent());

    checkFirstRunPresentation(savedInstanceState, missingPermissions);

    BroadcastReceiver r = new CancelReceiver();

    IntentFilter filter = new IntentFilter("com.remulasce.lametroapp.cancel_notification");
    this.registerReceiver(r, filter);
  }

  /**
   * Checks if we should show the autocomplete dropdown, focus the textview, etc. so the app is
   * ready for the user.
   */
  private void checkFirstRunPresentation(Bundle savedInstanceState, boolean missingPermissions) {
    if (!missingPermissions) {
      if (savedInstanceState == null) {
        maybeShowAutocompleteDropdown();
      } else {
        if (savedInstanceState.getBoolean("show_omnicomplete")) {
          showDropdownOnStart.run();
        }
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    state.putBoolean("show_omnicomplete", omniField.isPopupShowing());
  }

  private void setupRegionalization() {
    RegionalizationHelper.setPersistence(getFieldSaver());

    installedAgencyChecker = new InstalledAgencyChecker(this);
    Collection<Agency> installedAgencies = installedAgencyChecker.getInstalledAgencies();

    RegionalizationHelper.getInstance().setInstalledAgencies(installedAgencies);
    RegionalizationHelper.getInstance().loadPersistedAgencies();
  }

  /**
   * Requests permissions if necessary. If permissions were requested (we didn't have them), returns
   * true.
   */
  private boolean checkPermissionsNeeded() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ArrayList<String> permissions = new ArrayList<>();
      permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
      permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

      permissions = permissionsToRequest(permissions);

      if (permissions.isEmpty()) {
        return false;
      }
      requestPermissions(
          permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST_CODE);

      return true;
    }

    return false;
  }

  private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
    ArrayList<String> result = new ArrayList<>();

    for (String perm : wantedPermissions) {
      if (!hasPermission(perm)) {
        result.add(perm);
      }
    }

    return result;
  }

  private boolean hasPermission(String permission) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    return true;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      boolean permissionsSucceeded = true;
      for (int i = 0; i < permissions.length; i++) {
        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
          // Well, oops. Dunno what to do here.
          permissionsSucceeded = false;
          continue;
        }
      }

      if (permissionsSucceeded) {
        // Re-check location.
        maybeShowAutocompleteDropdown();
      } else {
        Toast.makeText(
                this,
                "Listen it's a transit arrivals app. It needs your location. "
                    + "But I'll try, at least, without it.",
                Toast.LENGTH_LONG)
            .show();
      }
    }
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
    locationService = new CachedLocationRetriever(this);
    GlobalLocationProvider.setRetriever(locationService);
  }

  private void setupTutorials() {
    tutorialManager = new AndroidTutorialManager(this, aboutPaneHint);
    TutorialManager.setTutorialManager(tutorialManager);
  }

  private void setupOmniBar() {
    autoCompleteAdapter =
        new OmniAutoCompleteAdapter(
            this,
            new UserStateProvider() {
              @Override
              public Collection<BasicLocation> getInterestingLocations() {
                ArrayList<BasicLocation> ret = new ArrayList<>();

                ret.addAll(requestFragment.getInterestingLocations());
                ret.addAll(currentLocationOrEmpty());

                return ret;
              }

              @Override
              public Collection<Stop> getCurrentlyTrackedStops() {
                return requestFragment.getCurrentlyTrackedStops();
              }

              @NonNull
              private List<BasicLocation> currentLocationOrEmpty() {
                BasicLocation currentLocation = locationService.getCurrentLocation();
                return currentLocation == null
                    ? Collections.<BasicLocation>emptyList()
                    : Collections.singletonList(currentLocation);
              }
            },
            R.layout.omnibar_dropdown_item,
            R.id.omnibar_item_station_name,
            staticsProvider,
            locationService,
            routeColorer);
    omniField.setAdapter(autoCompleteAdapter);
    omniField.setThreshold(0);

    omniHandler =
        new OmniBarInputHandler(
            omniField, clearButton, autocompleteProgress, requestFragment, staticsProvider, this);
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Toolbar Title");

    setSupportActionBar(toolbar);

    ImageButton navigationButton = findViewById(R.id.navigation_button);
    navigationButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mDrawerLayout.openDrawer(mDrawerLayout.findViewById(R.id.left_drawer));
          }
        });

    this.getLayoutInflater().inflate(R.layout.omni_search_box, toolbar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerLayout.addDrawerListener(
        new DrawerLayout.SimpleDrawerListener() {
          @Override
          public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            tutorialManager.aboutPaneOpened();

            Tracking.setScreenName("About Page");
            Tracking.sendEvent("About Page", "Pane Opened");
          }
        });
  }

  void setupAboutPage() {
    try {
      versionNumber.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
    } catch (PackageManager.NameNotFoundException e) {
      Log.w(TAG, "Version not found");
      versionNumber.setVisibility(View.GONE);
    }
  }

  void linkViewReferences() {
    omniField = (ProgressAutoCompleteTextView) findViewById(R.id.omni_text);
    clearButton = (Button) findViewById(R.id.omni_clear_button);
    legalButton = (Button) findViewById(R.id.legal_info_button);
    settingsButton = (Button) findViewById(R.id.settings_button);
    versionNumber = (TextView) findViewById(R.id.about_version_number);
    autocompleteProgress = (ProgressBar) findViewById(R.id.autocomplete_progress);
    aboutPaneHint = (TextView) findViewById(R.id.about_tutorial);

    tripList = (ListView) findViewById(R.id.tripList);
    tripListHint = (TextView) findViewById(R.id.trip_list_hint);
    tripListSecondaryHint = (TextView) findViewById(R.id.trip_list_secondary_hint);
    tripListProgress = (ProgressBar) findViewById(R.id.trip_list_progress);
    networkStatusView = findViewById(R.id.network_status_bar);

    requestFragment =
        (ServiceRequestListFragment)
            getSupportFragmentManager().findFragmentById(R.id.service_request_fragment);
  }

  void setupActionListeners() {
    requestHandler = new ServiceRequestHandler();
    tripPopulator =
        new TripPopulator(
            requestHandler, tripList, tripListHint, tripListSecondaryHint, tripListProgress, this);

    legalButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.legal_info_dialog_title))
                .setView(View.inflate(MainActivity.this, R.layout.legal_page, null))
                .show();
          }
        });

    settingsButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            DialogFragment settingsFragment = new RegionSettingsDialogFragment();
            settingsFragment.show(getFragmentManager(), "region_settings");
          }
        });
  }

  private void initializeStaticData() {
    staticsProvider = new MetroStaticsProvider(this, installedAgencyChecker);
    staticsProvider.initialize();

    routeColorer = new HardcodedRouteColors();

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
    Stop stop = new Stop(bundle.getStringExtra("StopID"));

    if (stop.isValid()) {
      // Early devices don't support notification actions
      // So this is the only way to disable arrival notification for them
      // Added in 4.1 (API 16)
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
        NotifyServiceManager.stopNotifyService(this);
      }
    }

    requestFragment.loadSavedRequests();

    String label = "Form Filled From Preferences";

    if (requestFragment.numRequests() > 0) {
      label = "Form Filled From Preferences";
    }

    Tracking.sendEvent("MainScreen", "Field Population", label);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onStart() {
    super.onStart();
    requestHandler.StartPopulating();
    tripPopulator.StartPopulating();
    tutorialManager.appStarted();
    PredictionManager.getInstance().resumeTracking();

    tutorialManager.userOpenedApp();
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
    tutorialManager.appStopped();
    tripPopulator.StopPopulating();
  }

  @Override
  protected void onResume() {
    super.onResume();

    Tracking.setScreenName("Main Screen");
    locationService.startLocating(this);
  }

  // Ugly hack to show history suggestions as soon as app loads
  // Except, Android won't actually tell you when it's ok with dialogs showing
  // So instead we check every xms until we actually have a window.
  private void maybeShowAutocompleteDropdown() {
    // The whole thing doesn't really work on Gingerbread.
    // Not that anyone actually still uses Gingerbread.
    // Check if we're in the about pane, cause that's whack
    if (requestHandler.getRequests().size() == 0
        && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      Handler h = new Handler(Looper.getMainLooper());
      h.postDelayed(showDropdownOnStart, 100);
    }
  }

  Runnable showDropdownOnStart =
      new Runnable() {
        @Override
        public void run() {
          if (omniField.getWindowVisibility() != View.GONE) { // && omniField.isFocused()) {
            Log.i(TAG, "Showing omni dropdown after startup");
            omniField.clearFocus();
            omniField.requestFocus();

            InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                omniField.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
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
        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
          omniField.clearFocus();
          //
          // Hide keyboard
          //
          InputMethodManager imm =
              (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);
        }
      }
    }
    return super.dispatchTouchEvent(event);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
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
