package com.remulasce.lametroapp.components;

import android.app.Activity;
import android.net.Uri;
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
import com.remulasce.lametroapp.types.ServiceRequest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceRequestFragment.OnServiceRequestListChanged} interface
 * to handle interaction events.
 * Use the {@link ServiceRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceRequestFragment extends Fragment {
    private static final String TAG = "ServiceRequestFragment";

    private OnServiceRequestListChanged mListener;
    private ListView requestList;

    private List<ServiceRequest> requests = new ArrayList<ServiceRequest>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceRequestFragment.
     */
    public static ServiceRequestFragment newInstance(TripPopulator populator) {
        ServiceRequestFragment fragment = new ServiceRequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayAdapter<ServiceRequest> makeAdapter(List<ServiceRequest> items) {
        return new ServiceRequestListAdapter(getActivity(), R.layout.service_request_item, items);
    }


    public void AddServiceRequest(ServiceRequest serviceRequest) {
        Log.d(TAG, "Adding service request " + serviceRequest);
        if (!requests.contains(serviceRequest)) {
            requests.add(serviceRequest);

            requestList.setAdapter(makeAdapter(requests));
            updateTripPopulator(requests);
            saveServiceRequests(requests);
        }
    }

    public ServiceRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_request_list, container, false);

        requestList = (ListView) view.findViewById(R.id.service_request_list);
        requestList.setOnItemClickListener(onItemClickListener);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnServiceRequestListChanged) activity;
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
        mListener.getTripPopulator().StopSelectionChanged(convertToStringLine(requests));
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

        requestList.setAdapter(makeAdapter(requests));
        this.updateTripPopulator(requests);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Log.d(TAG, "ServiceRequest Item clicked");
            ServiceRequest s = (ServiceRequest) adapterView.getItemAtPosition(pos);
            requests.remove(s);
            requestList.setAdapter(makeAdapter(requests));
            s.descope();

            updateTripPopulator(requests);
            saveServiceRequests(requests);
        }
    };

    // Temp method converts all the ServiceRequests into stringly typed STopIDs,
    // as would be expected from the old StopList.

    // This is a stop-gap until we convert the whole thing to ServiceRequests.
    private String convertToStringLine(List<ServiceRequest> requests) {
        StringBuilder s = new StringBuilder();

        for (ServiceRequest request : requests) {
            s.append(request.toString() + " ");
        }

        return s.toString();
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnServiceRequestListChanged {
        public TripPopulator getTripPopulator();
        public FieldSaver getFieldSaver();
    }

}
