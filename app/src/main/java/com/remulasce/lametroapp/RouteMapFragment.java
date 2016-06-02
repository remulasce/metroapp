package com.remulasce.lametroapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.BasicLocation;
import com.remulasce.lametroapp.java_core.basic_types.Shape;
import com.remulasce.lametroapp.java_core.basic_types.ShapePoint;
import com.remulasce.lametroapp.java_core.basic_types.ShapePoints;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.static_data.InstalledAgencyChecker;
import com.remulasce.lametroapp.static_data.SQLPreloadedRouteMapReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RouteMapFragment#newInstance} factory method to
 * create an instance of this mapFragment.
 */
public class RouteMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    public RouteMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this mapFragment using the provided parameters.
     *
     * @return A new instance of mapFragment RouteMapFragment.
     */
    public static RouteMapFragment newInstance() {
        return new RouteMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void applyLines() {
        final LatLng camLoc = map.getCameraPosition().target;
        map.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Agency agency = new InstalledAgencyChecker(getActivity()).getInstalledRouteMapAgencies().iterator().next();
                SQLPreloadedRouteMapReader mapReader = new SQLPreloadedRouteMapReader(getActivity(), "vta-routelines.db", agency);

                BasicLocation center = new BasicLocation(camLoc.latitude, camLoc.longitude);
                final Collection<Stop> nearbyStops = mapReader.getNearbyStops(center);

                RouteMapFragment.this.getView().post(new Runnable() {
                    @Override
                    public void run() {
                        for (Stop s : nearbyStops) {
                            BasicLocation loc = s.getLocation();
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(loc.latitude, loc.longitude))
                                    .title("M"));
                        }
                    }
                });

                // TODO: Should be a map shape -> shapeDistance, keeping only minimum distance shapedistance
                Set<Shape> shapeSet = new HashSet<>();

                for (Stop s : nearbyStops) {
                    Collection<SQLPreloadedRouteMapReader.ShapeDistance> shapes
                            = mapReader.getShapeIndexesForStop(s);
                    for (SQLPreloadedRouteMapReader.ShapeDistance sd : shapes) {
                        shapeSet.add(sd.shape);
                    }
                }

                final Set<ShapePoints> shapes = new HashSet<>();
                for (Shape s : shapeSet) {
                    shapes.add(mapReader.getShapePoints(s.getShapeId(), 0));
                }

                RouteMapFragment.this.getView().post(new Runnable() {
                    @Override
                    public void run() {
                        // Draw them
                        // (separated out for future multithreading
                        for (ShapePoints sp : shapes) {
                            PolylineOptions line = new PolylineOptions();
                            // Force A color to be fully visible.
                            line.color(sp.hashCode() | 0xFF000000);

                            for (ShapePoint point : sp.points) {
                                BasicLocation loc = point.loc;
                                line.add(new LatLng(loc.latitude, loc.longitude));
                            }

                            map.addPolyline(line);
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_map, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyLines();
            }
        });
        fab.show();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.398648, -122.104824), 17));

        applyLines();
    }
}
