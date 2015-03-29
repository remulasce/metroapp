package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;
import com.remulasce.lametroapp.java_core.static_data.StopNameTranslator;
import com.remulasce.lametroapp.static_data.AutoCompleteHistoryFiller;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/13/2015.
 *
 * This handles the omni textbar, add-request button, and clear button.
 * It just pushes new servicerequests to the service request list fragmentt.
 *
 * That means this handles the autocomplete stuff too.
 */
public class OmniBarInputHandler {
    private static final String TAG = "OmniBarInputHandler";

    private final ProgressAutoCompleteTextView omniField;
    private final ImageButton addButton;
    private final Button clearButton;
    private final ServiceRequestListFragment requestList;
    private final ProgressBar autocompleteProgress;
    private final StopNameTranslator stopNames;
    private final StopLocationTranslator stopLocations;
    private final AutoCompleteHistoryFiller autoCompleteHistory;
    private final Tracker t;

    //Poor form to require Context, we just need to show Toasts occasionally.
    private final Context c;

    public OmniBarInputHandler(ProgressAutoCompleteTextView textView, ImageButton addButton, Button clearButton,
                               ProgressBar autocompleteProgress,
                               ServiceRequestListFragment requestList, StopNameTranslator stopNames,
                               StopLocationTranslator locations, AutoCompleteHistoryFiller autoCompleteHistory,
                               Tracker t, Context c) {
        this.omniField = textView;
        this.addButton = addButton;
        this.autocompleteProgress = autocompleteProgress;
        this.clearButton = clearButton;
        this.requestList = requestList;
        this.stopNames = stopNames;
        this.stopLocations = locations;
        this.autoCompleteHistory = autoCompleteHistory;

        this.t = t;
        this.c = c;

        linkViewHandlers();
    }

    private void linkViewHandlers() {
        if (addButton != null) addButton.setOnClickListener(omniButtonListener);
        clearButton.setOnClickListener(clearButtonListener);
        omniField.setOnEditorActionListener(omniDoneListener);
        omniField.setOnItemClickListener(autocompleteSelectedListener);

        omniField.setLoadingIndicator(autocompleteProgress);
    }

    private final AdapterView.OnItemClickListener autocompleteSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Tracking.sendEvent("AutoComplete", "AutoComplete Selected");
            long t = Tracking.startTime();

            OmniAutoCompleteEntry entry = (OmniAutoCompleteEntry) adapterView.getItemAtPosition(i);
            autoCompleteHistory.autocompleteSaveSelection(entry);

            String requestText = omniField.getText().toString();
            makeServiceRequestFromOmniInput(requestText);

            Tracking.sendUITime("OmniBarInputHandler", "omniSelectedListener", t);


            keepOpenDropdown();
        }
    };

    private void keepOpenDropdown() {
        final Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                omniField.requestFocus();
                InputMethodManager inputMethodManager=(InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(omniField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                omniField.showDropDown();

                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        omniField.clearFocus();

                        InputMethodManager imm = (InputMethodManager)c.getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);
//                        omniField.dismissDropDown();
                    }
                }, 2000);
            }
        }, 10);

    }

    private final TextView.OnEditorActionListener omniDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            long t = Tracking.startTime();

            String requestText = textView.getText().toString();
            makeServiceRequestFromOmniInput(requestText);

            Tracking.sendUITime("OmniBarInputHandler", "omniDoneListener", t);
            return true;
        }
    };
    private final View.OnClickListener omniButtonListener = new View.OnClickListener() {
        public void onClick( View v ) {
            long t = Tracking.startTime();

            String requestText = omniField.getText().toString();
            makeServiceRequestFromOmniInput(requestText);

            Tracking.sendUITime("OmniBarInputHandler", "omniButtonListener", t);
        }
    };
    private final View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearFields();
        }
    };

    // This is an extremely low level check. The ServiceRequest itself will have a better
    // idea whether it can actually track anything.
    private boolean isOmniInputValid(String input) {
        return input != null && !input.isEmpty();
    }

    private void makeServiceRequest( String stopID, String displayName ) {
        Log.d(TAG, "Making service request from stopID: "+stopID+", display: "+displayName);

        Stop add = new Stop(stopID);
        add.setLocation(stopLocations.getStopLocation(add));

        ServiceRequest serviceRequest = new StopServiceRequest(add, displayName);

        if (serviceRequest.isValid()) {
            requestList.AddServiceRequest(serviceRequest);
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
        }
    }
    private ServiceRequest makeMultiStopServiceRequest( Collection<String> stopIDs, String displayName ) {
        Log.d(TAG, "Making service request from stopID: "+stopIDs+", display: "+displayName);

        Collection<Stop> stops = new ArrayList<Stop>();

        for (String stop : stopIDs) {
            stops.add(new Stop(stop));
        }

        ServiceRequest serviceRequest = new StopServiceRequest(stops, displayName);

        if (serviceRequest.isValid()) {
            return serviceRequest;
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
            return null;
        }
    }

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

                    Tracking.sendEvent("AutoComplete", "AutoComplete Add", "StopID");
                }
                // It was a valid stop name
                else if (convertedID != null && !convertedID.isEmpty()) {
                    ServiceRequest request = makeMultiStopServiceRequest(convertedID, requestText);

                    if (request != null) {
                        requestList.AddServiceRequest(request);
                    }

                    omniField.getEditableText().clear();
                    omniField.clearFocus();

                    InputMethodManager imm = (InputMethodManager)c.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);

                    Tracking.sendEvent("AutoComplete", "AutoComplete Add", "StopName");
                }
                // Not valid.
                else {
                    Log.i(TAG, "Couldn't parse omnibox input into id or stopname, ignoring");
                    Toast.makeText(c, "Invalid stop name", Toast.LENGTH_SHORT).show();

                    Tracking.sendEvent("AutoComplete", "AutoComplete Add", "Invalid");
                }
            } catch (Exception e) {
                Tracking.sendEvent("AutoComplete", "AutoComplete Add", "Exception");
            }
        }
    }

    public void clearFields() {
        omniField.setText("");
        Tracking.sendEvent("MainScreen", "Clear Fields");
    }
}
