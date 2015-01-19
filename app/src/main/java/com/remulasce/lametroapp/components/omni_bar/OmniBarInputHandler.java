package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.remulasce.lametroapp.components.servicerequest_list.ServiceRequestListFragment;
import com.remulasce.lametroapp.static_data.StopNameTranslator;
import com.remulasce.lametroapp.basic_types.ServiceRequest;

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

    private AutoCompleteTextView omniField;
    private ImageButton addButton;
    private Button clearButton;
    private ServiceRequestListFragment requestList;
    private StopNameTranslator stopNames;
    private Tracker t;

    //Poor form to require Context, we just need to show Toasts occasionally.
    private Context c;

    public OmniBarInputHandler(AutoCompleteTextView textView, ImageButton addButton, Button clearButton,
                               ServiceRequestListFragment requestList, StopNameTranslator stopNames,
                               Tracker t, Context c) {
        this.omniField = textView;
        this.addButton = addButton;
        this.clearButton = clearButton;
        this.requestList = requestList;
        this.stopNames = stopNames;

        this.t = t;
        this.c = c;

        linkViewHandlers();
    }

    private void linkViewHandlers() {
        addButton.setOnClickListener(omniButtonListener);
        clearButton.setOnClickListener(clearButtonListener);
        omniField.setOnEditorActionListener(omniDoneListener);
    }

    protected TextView.OnEditorActionListener omniDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            String requestText = textView.getText().toString();
            makeServiceRequestFromOmniInput(requestText);

            return true;
        }
    };
    protected View.OnClickListener omniButtonListener = new View.OnClickListener() {
        public void onClick( View v ) {
            String requestText = omniField.getText().toString();
            makeServiceRequestFromOmniInput(requestText);
        }
    };
    protected  View.OnClickListener clearButtonListener = new View.OnClickListener() {
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
        ServiceRequest serviceRequest = new ServiceRequest(stopID);
//        serviceRequest.setDisplayName(displayName+ ", "+stopID);
        serviceRequest.setDisplayName(displayName);

        if (serviceRequest.isValid()) {
            requestList.AddServiceRequest(serviceRequest);
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
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
                    Toast.makeText(c, "Invalid stopname or id", Toast.LENGTH_SHORT).show();

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

    public void clearFields() {
        omniField.setText("");
        t.send( new HitBuilders.EventBuilder()
                .setCategory( "MainScreen" )
                .setAction( "Clear Fields" )
                .build() );
    }
}
