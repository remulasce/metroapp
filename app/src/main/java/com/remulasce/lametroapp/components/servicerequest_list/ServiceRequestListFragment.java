package com.remulasce.lametroapp.components.servicerequest_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.components.tutorial.TutorialManager;
import com.remulasce.lametroapp.java_core.ServiceRequestHandler;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.components.persistence.FieldSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
  List of active ServiceRequests
  Handles adding and removing them from the TripPopulator
 */
public class ServiceRequestListFragment extends Fragment {
    private static final String TAG = "ServiceRequestFragment";

    private ServiceRequestListFragmentSupport mListener;
    private ListView requestList;
    private TextView hintText;
    private TextView secondaryHintText;

    private final List<ServiceRequest> requests = new ArrayList<ServiceRequest>();

    private ArrayAdapter<ServiceRequest> makeAdapter(List<ServiceRequest> items) {
        //noinspection unchecked
        return new ServiceRequestListAdapter(getActivity(), R.layout.service_request_item, items, onCancelListener);
    }

    public void AddServiceRequest(ServiceRequest serviceRequest) {
        if (duplicateRequest(serviceRequest)) {
            Log.w(TAG, "Ignored duplicate service request");
            return;
        }

        Log.d(TAG, "Adding service request " + serviceRequest);
        requests.add(serviceRequest);

        requestsChanged(true);
    }

    private boolean duplicateRequest(ServiceRequest serviceRequest) {
        for (ServiceRequest r : requests ) {
            if (r.getDisplayName().equals(serviceRequest.getDisplayName())) {
                return true;
            }
        }
        return false;
    }

    private void requestsChanged(boolean saveRequests) {
        requestList.setAdapter(makeAdapter(requests));
        updateTripPopulator(requests);
        if (saveRequests) {
            saveServiceRequests(requests);
        }

        updateHintVisibility();
    }

    private void updateHintVisibility() {
        if (requests.size() > 0) {
            hintText.setVisibility(View.INVISIBLE);
            secondaryHintText.setVisibility(View.INVISIBLE);
        } else {
            if (TutorialManager.getInstance().requestListNeedsHint()) {
                hintText.setVisibility(View.VISIBLE);
                secondaryHintText.setVisibility(View.INVISIBLE);
            } else {
                secondaryHintText.setVisibility(View.VISIBLE);
                hintText.setVisibility(View.INVISIBLE);
            }
        }
    }

    public int numRequests() {
        return requests.size();
    }

    public ServiceRequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_request_list, container, false);

        requestList = (ListView) view.findViewById(R.id.service_request_list);
        hintText = (TextView) view.findViewById(R.id.service_request_hint_text);
        secondaryHintText = (TextView) view.findViewById(R.id.request_list_secondary_hint);

        requestList.setOnItemClickListener(onItemClickListener);
        updateHintVisibility();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ServiceRequestListFragmentSupport) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnServiceRequestListChanged");
        }
    }

    @Override
    public void onDetach() {
        saveServiceRequests(requests);

        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();

        saveServiceRequests(requests);
    }

    private void updateTripPopulator(List<ServiceRequest> requests) {
        requestList.setOnItemClickListener(onItemClickListener);
        mListener.getTripPopulator().SetServiceRequests(requests);
    }

    // Saves the current servicerequests so they can be recreated after the app has closed.
    private void saveServiceRequests(List<ServiceRequest> requests) {
        Log.d(TAG, "Saving service requests");
        mListener.getFieldSaver().saveServiceRequests(requests);
    }

    public void loadSavedRequests() {
        Log.d(TAG, "Loading saved requests");

        clearAllRequests(false);
        Collection<ServiceRequest> serviceRequests = mListener.getFieldSaver().loadServiceRequests();
        if (serviceRequests != null && !serviceRequests.contains(null)) {
            this.requests.addAll(serviceRequests);
        } else {
            Log.w(TAG, "Saved requests loaded something it shouldn't have!");
        }

        requestsChanged(false);
    }

    private void clearAllRequests(boolean saveRequests) {
        for (ServiceRequest each : requests) {
            each.descope();
        }
        requests.clear();
        requestsChanged(saveRequests);
    }

    private void raiseRequestClickedDialog(final ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Restore Arrivals");
        builder.setMessage("Show all arrivals to this stop, restoring destinations that had been swiped away?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                request.restoreTrips();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Log.d(TAG, "ServiceRequest Item clicked");
            ServiceRequest s = (ServiceRequest) adapterView.getItemAtPosition(pos);

            raiseRequestClickedDialog(s);

            TutorialManager.getInstance().tripDismissalUndone();
        }
    };

    private void cancelRequest(ServiceRequest s) {
        Log.d(TAG, "Cancelling request: "+s);
        s.descope();
        s.cancelRequest();
        requests.remove(s);
        requestsChanged(true);
    }

    private final View.OnClickListener onCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ServiceRequest request = (ServiceRequest) view.getTag();
            cancelRequest(request);
        }
    };

    public interface ServiceRequestListFragmentSupport {
        public ServiceRequestHandler getTripPopulator();
        public FieldSaver getFieldSaver();
    }

}
