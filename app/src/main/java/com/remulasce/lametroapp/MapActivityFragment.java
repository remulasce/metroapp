package com.remulasce.lametroapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng[] testline = new LatLng[] {
            new LatLng(37.423742, -122.089998),
            new LatLng(37.421009, -122.086690),
            new LatLng(37.420525, -122.077949),
            new LatLng(37.406214, -122.078102),
    };

    public MapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng google = new LatLng(37.4164323,-122.0761531);
        mMap.addMarker(new MarkerOptions().position(google).title("Marker on Google"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(google));

        PolylineOptions options = new PolylineOptions();
        for ( int i = 0; i < testline.length; i++) {
            options.add(testline[i]);
        }
        Polyline line = googleMap.addPolyline(options);
    }
}
