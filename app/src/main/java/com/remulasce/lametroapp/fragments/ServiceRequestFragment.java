package com.remulasce.lametroapp.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.types.ServiceRequest;

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
    public static ServiceRequestFragment newInstance() {
        ServiceRequestFragment fragment = new ServiceRequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayAdapter<ServiceRequest> makeAdapter(List<ServiceRequest> items) {
        return new ArrayAdapter<ServiceRequest>(getActivity(), android.R.layout.simple_list_item_1, items);
    }

    public void AddServiceRequest(ServiceRequest serviceRequest) {
        Log.d(TAG, "Adding service request " + serviceRequest);
        if (!requests.contains(serviceRequest)) {
            requests.add(serviceRequest);

            requestList.setAdapter(makeAdapter(requests));
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


        String[] items = { "Test 1", "Test 2", "Test 3" };
        requestList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
