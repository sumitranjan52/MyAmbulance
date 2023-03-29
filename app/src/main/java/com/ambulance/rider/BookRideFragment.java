/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.rider;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambulance.rider.Backend.RideRequest;
import com.ambulance.rider.Common.AlertDialogBox;
import com.ambulance.rider.Common.Common;
import com.ambulance.rider.Common.NetworkErrorMessages;
import com.ambulance.rider.Interfaces.AmbulanceDialogInterface;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.ambulance.rider.Singleton.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookRideFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private MapView mMapView;
    private GoogleMap mMap;

    private LinearLayout beforeBooking, betweenBookAccept, afterBooking;
    private TextView driverName, driverPhone, driverVehicle, cancelRide;
    private Button btnConfirmBooking, btnCancelBooking;

    private Context mContext;
    private Activity activity;

    private double lat, lng;

    private AlertDialog dialog;

    private Handler ambulanceTrackerHandler;
    private final long SEARCH_DELAY = 5000;

    public BookRideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MAP_FRAG", "onAttach");
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        activity.setTitle("Book Ambulance");
        ambulanceTrackerHandler = new Handler();
        Log.d("MAP_FRAG", "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_ride, container, false);

        beforeBooking = view.findViewById(R.id.beforeBooking);
        betweenBookAccept = view.findViewById(R.id.betweenBookAccept);
        afterBooking = view.findViewById(R.id.afterBooking);
        driverName = view.findViewById(R.id.driverDetails);
        driverPhone = view.findViewById(R.id.callDriver);
        driverVehicle = view.findViewById(R.id.vehicleDetails);
        cancelRide = view.findViewById(R.id.cancelRide);
        btnConfirmBooking = view.findViewById(R.id.btnConfirmBook);
        btnCancelBooking = view.findViewById(R.id.btnCancelBook);

        driverPhone.setOnClickListener(this);
        cancelRide.setOnClickListener(this);
        btnConfirmBooking.setOnClickListener(this);
        btnCancelBooking.setOnClickListener(this);

        if (Common.isBooked){
            beforeBooking.setVisibility(View.GONE);
            betweenBookAccept.setVisibility(View.VISIBLE);
            afterBooking.setVisibility(View.GONE);
            if(Common.isBookingAccepted) {
                driverName.setText(Common.driverName);
                driverVehicle.setText(Common.ambulanceNo);
                beforeBooking.setVisibility(View.GONE);
                betweenBookAccept.setVisibility(View.GONE);
                afterBooking.setVisibility(View.VISIBLE);
            }
        }

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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP_FRAG", "MapCallback");

        mMap = googleMap;

        Log.d("MAP", mMap.toString());

        mMap.setPadding(0, 0, 0, 150);

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude, Common.longitude), 15.0f));
    }

    Runnable ambulanceTracking = new Runnable() {
        @Override
        public void run() {
            try {
                trackAmbulance();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ambulanceTrackerHandler.postDelayed(ambulanceTracking, SEARCH_DELAY);
            }
        }
    };

    public void startTracking() {
        ambulanceTracking.run();
    }

    public void stopTracking() {
        ambulanceTrackerHandler.removeCallbacks(ambulanceTracking);
    }

    @Override
    public void onClick(View v) {
        String userId = mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId", "");
        switch (v.getId()) {

            case R.id.btnConfirmBook:
                dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("Booking ambulance", "Please wait...", false);
                fnBookRide(userId);
                break;

            case R.id.btnCancelBook:
            case R.id.cancelRide:
                dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("Cancelling ambulance request", "Please wait...", false);
                fnCancelRide(userId);
                break;

            case R.id.callDriver:
                fnCallDriver();
                break;

        }
    }

    private void fnCallDriver() {

        Intent callDriver = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Common.driverPhone));
        startActivity(callDriver);
    }

    private void fnCancelRide(String userId) {

        RideRequest rideRequest = new RideRequest();
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.setUserId(userId);
        rideRequest.cancelRideAndUpdateServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                dialog.dismiss();
                if (response.has("response")) {
                    Common.isBooked = false;
                    Common.isBookingAccepted = false;
                    stopTracking();
                    MainActivity.fragmentManager.popBackStack();
                } else {
                    Log.d("TAG", "onSuccess: " + response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                try {
                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                            , true, "Ok"
                            , new AmbulanceDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void populateDriverDetails() {
        driverName.setText(Common.driverName);
        driverVehicle.setText(Common.ambulanceNo);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                beforeBooking.setVisibility(View.GONE);
                betweenBookAccept.setVisibility(View.GONE);
                afterBooking.setVisibility(View.VISIBLE);
            }
        });
        startTracking();
    }
    private void fnBookRide(String userId) {

        RideRequest rideRequest = new RideRequest();
        rideRequest.setBooking_lat(Common.latitude);
        rideRequest.setBooking_lng(Common.longitude);
        rideRequest.setUserId(userId);
        rideRequest.requestRide(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.has("response")) {
                        JSONObject jsonObject = response.getJSONObject("response");
                        Common.emergencyRequestBookingID = jsonObject.getString("id");
                        activity.setTitle("Ambulance Booked");
                        beforeBooking.setVisibility(View.GONE);
                        betweenBookAccept.setVisibility(View.VISIBLE);
                        afterBooking.setVisibility(View.GONE);
                        Common.isBooked = true;
                    } else {
                        Log.d("TAG", "onSuccess: " + response.toString());
                        Toast.makeText(mContext, RideRequest.errorResponseMsg(response.getString("invalid")), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                try {
                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                            , true, "Ok"
                            , new AmbulanceDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void trackAmbulance() {

        JSONObject parameter = new JSONObject();
        try {
            parameter.put("bookingId", Common.emergencyRequestBookingID);
            parameter.put("key", Common.API_KEY);
            parameter.put("userId", mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest ambulanceTracker = new JsonObjectRequest(Request.Method.PATCH, (Common.BASE_URL + Common.REQUEST_URL)
                , parameter
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("response")) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("response");
                        if (!jsonObject.getString("status").equals("booked") && !jsonObject.getString("status").equals("completed")) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                            lat = Double.parseDouble(jsonObject.getString("driverLat"));
                            lng = Double.parseDouble(jsonObject.getString("driverLng"));
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_direction_car))
                                    .position(new LatLng(lat, lng)));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(Common.latitude, Common.longitude)).include(new LatLng(lat, lng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("TRACKER", "onResponse: " + response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                            , true, "Ok"
                            , new AmbulanceDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        );
        ambulanceTracker.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(ambulanceTracker);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Common.isBooked && Common.isBookingAccepted){
            stopTracking();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isBooked && Common.isBookingAccepted){
            startTracking();
        }
    }
}

