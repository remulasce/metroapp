package com.remulasce.lametroapp.components.omni_bar;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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
    private final Button clearButton;
    private final ServiceRequestListFragment requestList;
    private final ProgressBar autocompleteProgress;
    private final AutoCompleteHistoryFiller autoCompleteHistory;

    //Poor form to require Context, we just need to show Toasts occasionally.
    private final Context c;

    public OmniBarInputHandler(ProgressAutoCompleteTextView textView,
                               Button clearButton,
                               ProgressBar autocompleteProgress,
                               ServiceRequestListFragment requestList,
                               AutoCompleteHistoryFiller autoCompleteHistory,
                               Context c) {
        this.omniField = textView;
        this.autocompleteProgress = autocompleteProgress;
        this.clearButton = clearButton;
        this.requestList = requestList;
        this.autoCompleteHistory = autoCompleteHistory;

        this.c = c;

        linkViewHandlers();
    }

    private void linkViewHandlers() {
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


            ServiceRequest request = makeMultiStopServiceRequest(entry.getStops(), entry.toString());

            if (request != null) {
                requestList.AddServiceRequest(request);
            }

            omniField.getEditableText().clear();
            omniField.clearFocus();

            InputMethodManager imm = (InputMethodManager)c.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);

            Tracking.sendEvent("AutoComplete", "AutoComplete Add", "Omni Selection");
            Tracking.sendUITime("OmniBarInputHandler", "omniSelectedListener", t);
        }
    };

    private final TextView.OnEditorActionListener omniDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            long t = Tracking.startTime();

            Tracking.sendUITime("OmniBarInputHandler", "omniDoneListener", t);
            return true;
        }
    };

    private final View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearFields();
        }
    };

    private ServiceRequest makeMultiStopServiceRequest( Collection<Stop> stops, String displayName ) {
        Log.d(TAG, "Making service request from stops: "+stops+", display: "+displayName);

        ServiceRequest serviceRequest = new StopServiceRequest(stops, displayName);

        if (serviceRequest.isValid()) {
            return serviceRequest;
        } else {
            Log.w(TAG, "Created invalid servicerequest, not adding to list");
            return null;
        }
    }

    public void clearFields() {

        boolean wasEmpty = omniField.getText().toString().isEmpty();

        omniField.setText("");
        // textChanged from setText launches another dropdown.
        // But if the text was already empty, then hide the dropdown, since why else did the user
        // hit the X button
        if (wasEmpty) {
            hideSoftKeyboard(omniField);

            omniField.dismissDropDown();
            omniField.clearFocus();
        }
        Tracking.sendEvent("MainScreen", "Clear Fields");
    }

    private void hideSoftKeyboard(View view) {
        // Hide soft keyboard- https://stackoverflow.com/questions/1109022
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(omniField.getWindowToken(), 0);
    }

}
