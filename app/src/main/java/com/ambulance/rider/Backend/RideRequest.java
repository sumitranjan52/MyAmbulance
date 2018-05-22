/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.rider.Backend;

import android.content.Context;

import com.ambulance.rider.Common.Common;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.ambulance.rider.Singleton.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RideRequest {

    private Context context;
    private String userId;
    private String bookingId;
    private double booking_lat, booking_lng;

    public RideRequest() {
    }

    public RideRequest(Context context, String bookingId) {
        this.context = context;
        this.bookingId = bookingId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getBooking_lat() {
        return booking_lat;
    }

    public void setBooking_lat(double booking_lat) {
        this.booking_lat = booking_lat;
    }

    public double getBooking_lng() {
        return booking_lng;
    }

    public void setBooking_lng(double booking_lng) {
        this.booking_lng = booking_lng;
    }

    /* Operation/Communication to/from server goes below */

    public void getRideDetailFromServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            if (this.bookingId != null) {
                jsonToken.put("bookingId", this.bookingId);
            }
            if (this.userId != null) {
                jsonToken.put("userId", this.userId);
            }
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest rideDetailsJSON = new JsonObjectRequest(Request.Method.PATCH, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        rideDetailsJSON.setRetryPolicy(new DefaultRetryPolicy(30*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(rideDetailsJSON);

    }

    public void requestRide(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("userId", this.userId);
            jsonToken.put("latitude", this.booking_lat);
            jsonToken.put("longitude", this.booking_lng);
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest rideRequestJSON = new JsonObjectRequest(Request.Method.POST, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        rideRequestJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(rideRequestJSON);

    }

    public void cancelRideAndUpdateServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("bookingId", this.bookingId);
            jsonToken.put("status", "cancelledByRider");
            jsonToken.put("userId", this.userId);
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest cancelRideJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        cancelRideJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(cancelRideJsonObjectRequest);

    }

    public static String errorResponseMsg(String msgCode) {

        switch (msgCode) {

            case "API_KEY_REQUIRED":
                return "Contact developer with error: API_KEY_REQUIRED on sumitranjan52@gmail.com";

            case "API_KEY_INVALID":
                return "Contact developer with error: API_KEY_INVALID on sumitranjan52@gmail.com";

            case "INVALID_INPUT":
                return "Contact developer with error: INVALID_INPUT on sumitranjan52@gmail.com";

            case "UPDATED":
                return "Record is updated into remote database.";

            case "NOTHING_CHANGED":
                return "Nothing changed in remote database.";

            case "EMPTY_BOOKING_ID":
                return "Booking id is not sent to server.";

            case "NO_BOOKING_FOUND":
                return "No such booking is made by any user.";

            case "EMPTY_USER_ID":
                return "User Id is not set. Try logging in again";

            case "EMPTY_DRIVER_ID":
                return "Driver Id is not set. Try logging in again";

            case "EMPTY_LATITUDE":
                return "You can not book ambulance without providing your location.";

            case "EMPTY_LONGITUDE":
                return "You can not book ambulance without providing your location.";

            case "NO_DRIVER_FOUND":
                return "No nearby ambulance is found";

            case "RIDE_BOOKED":
                return "Congratulations! Ambulance is booked for you.";

            case "RIDE_BOOKING_FAILED":
                return "We are sorry. We are unable to book ambulance for you.";

            case "NO_LOCATION_SPECIFIED":
                return "Please allow app to send your location to server, when needed";

            case "EMPTY_STATUS":
                return "Please provide status of the booking.";

            case "NO_USER_SPECIFIED":
                return "Some one should take responsibility of this booking";

            default:
                return "Something went wrong";

        }

    }

}
