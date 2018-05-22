package com.ambulance.rider;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ambulance.rider.Backend.LocationToServer;
import com.ambulance.rider.Common.Common;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private MapView mMapView;
    private GoogleMap mMap;

    private Button privateAM, govAM, btnBookRide;

    private BroadcastReceiver broadcastReceiver;

    private Context mContext;
    private Activity activity;

    private Handler driverSearchHandler;
    private final long SEARCH_DELAY = 5000;

    public HomeMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        driverSearchHandler = new Handler();
        Log.d("MAP_FRAG", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("MAP_FRAG", "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_map, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        privateAM = view.findViewById(R.id.privateAM);
        govAM = view.findViewById(R.id.govAM);
        btnBookRide = view.findViewById(R.id.btnBookRide);

        privateAM.setOnClickListener(this);
        govAM.setOnClickListener(this);
        btnBookRide.setOnClickListener(this);

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    Runnable driverSearching = new Runnable() {
        @Override
        public void run() {
            try {
                getAllDriversInRange(Common.latitude, Common.longitude);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                driverSearchHandler.postDelayed(driverSearching, SEARCH_DELAY);
            }
        }
    };

    public void startDriverSearching() {
        driverSearching.run();
    }

    public void stopDriverSearching() {
        driverSearchHandler.removeCallbacks(driverSearching);
    }

    /**
     * Manipulates the main once available.
     * This callback is triggered when the main is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP_FRAG", "MapCallback");

        mMap = googleMap;

        Log.d("MAP", mMap.toString());

        mMap.setPadding(0, 0, 0, 300);

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude, Common.longitude), 15.0f));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MAP_FRAG", "onAttach");
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MAP_FRAG", "onResume");
        mMapView.onResume();
        activity.setTitle("Ambulance");
        startDriverSearching();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        Common.latitude = intent.getExtras().getDouble("lat");
                        Common.longitude = intent.getExtras().getDouble("lng");
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude, Common.longitude), 15.0f));

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    private void getAllDriversInRange(double latitude, double longitude) {

        LocationToServer locationToServer = new LocationToServer(mContext);
        locationToServer.setLat(latitude);
        locationToServer.setLng(longitude);
        locationToServer.getLocationFromServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.has("response")) {

                    try {
                        btnBookRide.setEnabled(true);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                        JSONArray jsonArray = response.getJSONArray("response");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (i == jsonArray.length() - 1) {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(new LatLng(Common.latitude, Common.longitude))
                                        .include(new LatLng(Double.parseDouble(jsonObject.getString("latitude"))
                                                , Double.parseDouble(jsonObject.getString("longitude"))));

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(jsonObject.getString("latitude"))
                                                , Double.parseDouble(jsonObject.getString("longitude"))))
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_direction_car)));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);
                                mMap.moveCamera(cameraUpdate);
                            }
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(jsonObject.getString("latitude"))
                                            , Double.parseDouble(jsonObject.getString("longitude"))))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_direction_car)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "onSuccess: " + response.toString());

                } else {
                    btnBookRide.setEnabled(false);
                    Log.d("TAG", "onSuccess: " + response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                btnBookRide.setEnabled(false);
                error.printStackTrace();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MAP_FRAG", "onPause");
        stopDriverSearching();
        mMapView.onPause();
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MAP_FRAG", "onDestroy");
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MAP_FRAG", "onLowMemory");
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:

                // mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

                break;

            case R.id.privateAM:

                getAllDriversInRange(Common.latitude, Common.longitude);

                break;

            case R.id.govAM:


                break;

            case R.id.btnBookRide:


                MainActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .replace(R.id.mainFragmentContainer, new BookRideFragment())
                        .addToBackStack("myAmbulance")
                        .commit();


                break;
        }
    }
}
