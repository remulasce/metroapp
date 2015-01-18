package com.remulasce.lametroapp.components.servicerequest_list;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.TripPopulator;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.components.persistence.FieldSaver;

import java.util.ArrayList;
import java.util.List;


/**
  List of active ServiceRequests
  Handles adding and removing them from the TripPopulator
 */
public class ServiceRequestListFragment extends Fragment {
    private static final String TAG = "ServiceRequestFragment";

    private ServiceRequestListFragmentSupport mListener;
    private ListView requestList;

    private List<ServiceRequest> requests = new ArrayList<ServiceRequest>();

    private ArrayAdapter<ServiceRequest> makeAdapter(List<ServiceRequest> items) {
        //noinspection unchecked
        return new ServiceRequestListAdapter(getActivity(), R.layout.service_request_item, items, onCancelListener);
    }

    public void AddServiceRequest(ServiceRequest serviceRequest) {
        Log.d(TAG, "Adding service request " + serviceRequest);
        if (!requests.contains(serviceRequest)) {
            requests.add(serviceRequest);

            requestsChanged();
        }
    }

    private void requestsChanged() {
        requestList.setAdapter(makeAdapter(requests));
        updateTripPopulator(requests);
        saveServiceRequests(requests);
    }

    public ServiceRequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_request_list, container, false);

        requestList = (ListView) view.findViewById(R.id.service_request_list);
//        requestList.setOnItemClickListener(onItemClickListener);
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
        super.onDetach();
        mListener = null;
    }

    private void updateTripPopulator(List<ServiceRequest> requests) {
//        mListener.getTripPopulator().StopSelectionChanged(convertToStringLine(requests));
        mListener.getTripPopulator().SetServiceRequests(requests);
    }

    // Saves the current servicerequests so they can be recreated after the app has closed.
    private void saveServiceRequests(List<ServiceRequest> requests) {
        Log.d(TAG, "Saving service requests");
        mListener.getFieldSaver().saveServiceRequests(requests);
    }

    public void loadSavedRequests() {
        Log.d(TAG, "Loading saved requests");

        this.requests.clear();
        this.requests.addAll(mListener.getFieldSaver().loadServiceRequests());

        requestsChanged();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Log.d(TAG, "ServiceRequest Item clicked");
            ServiceRequest s = (ServiceRequest) adapterView.getItemAtPosition(pos);
            cancelRequest(s);
        }
    };

    private void cancelRequest(ServiceRequest s) {
        s.descope();
        requests.remove(s);
        requestsChanged();
    }

    private View.OnClickListener onCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ServiceRequest request = (ServiceRequest) view.getTag();
            cancelRequest(request);
        }
    };

    // Temp method converts all the ServiceRequests into stringly typed STopIDs,
    // as would be expected from the old StopList.
    // This is a stop-gap until we convert the whole thing to ServiceRequests.
    private String convertToStringLine(List<ServiceRequest> requests) {
        StringBuilder s = new StringBuilder();

        for (ServiceRequest request : requests) {
            s.append(request.toString());
            s.append(" ");
        }

        return s.toString();
    }

    public interface ServiceRequestListFragmentSupport {
        public TripPopulator getTripPopulator();
        public FieldSaver getFieldSaver();
    }

}
