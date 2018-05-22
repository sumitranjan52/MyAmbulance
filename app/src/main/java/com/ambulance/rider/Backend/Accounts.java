package com.ambulance.rider.Backend;

import android.content.Context;

import com.ambulance.rider.Common.Common;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.ambulance.rider.Model.Rider;
import com.ambulance.rider.Singleton.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sumit on 22-Feb-18.
 */

public class Accounts {

    private Rider rider;
    private Context context;

    public Accounts() {
    }

    public Accounts(Context context) {
        this.context = context;
    }

    public Accounts(Context context, Rider rider) {
        this.rider = rider;
        this.context = context;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public void loginRider(final VolleyJSONResponses callback) {

        JSONObject loginJSON = new JSONObject();
        try {
            loginJSON.put("username", rider.getUsername());
            loginJSON.put("password", rider.getPassword());
            loginJSON.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginHttpRequestForJSON = new JsonObjectRequest(Request.Method.PATCH
                , (Common.BASE_URL + Common.RIDER_URL), loginJSON
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
        loginHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(loginHttpRequestForJSON);

    }

    public void registerRider(final VolleyJSONResponses callback) {

        final JSONObject registerJSON = new JSONObject();
        try {
            registerJSON.put("key", Common.API_KEY);
            registerJSON.put("username", rider.getUsername());
            registerJSON.put("password", rider.getPassword());
            registerJSON.put("name", rider.getName());
            registerJSON.put("email", rider.getEmail());
            registerJSON.put("phone", rider.getPhone());
            registerJSON.put("blood-group", rider.getBloodGroup());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest registerHttpRequestForJSON = new JsonObjectRequest(Request.Method.POST
                , (Common.BASE_URL + Common.RIDER_URL), registerJSON
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
        registerHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(registerHttpRequestForJSON);
    }

    public void checkUserId(String userId, final VolleyJSONResponses callback){

        JSONObject loginIdJSON = new JSONObject();
        try {
            loginIdJSON.put("driverId", userId);
            loginIdJSON.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginIdHttpRequestForJSON = new JsonObjectRequest(Request.Method.PATCH
                , (Common.BASE_URL + Common.RIDER_URL), loginIdJSON
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
        loginIdHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(loginIdHttpRequestForJSON);

    }

    public void resetPassword(final VolleyJSONResponses callback){

        JSONObject resetJSON = new JSONObject();
        try {
            resetJSON.put("username", rider.getUsername());
            resetJSON.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest resetHttpRequestForJSON = new JsonObjectRequest(Request.Method.PATCH
                , (Common.BASE_URL + Common.RIDER_URL), resetJSON
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
        resetHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(resetHttpRequestForJSON);

    }

    public void updateRiderDetails(final VolleyJSONResponses callback) {

        JSONObject updateJSON = new JSONObject();
        try {
            updateJSON.put("key", Common.API_KEY);
            updateJSON.put("username", rider.getUsername());
            updateJSON.put("password", rider.getPassword());
            updateJSON.put("name", rider.getName());
            updateJSON.put("email", rider.getEmail());
            updateJSON.put("phone", rider.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateHttpRequestForJSON = new JsonObjectRequest(Request.Method.PUT
                , (Common.BASE_URL + Common.RIDER_URL), updateJSON
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
        updateHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(updateHttpRequestForJSON);

    }

    public void deleteDriver(final VolleyJSONResponses callback) {

        JSONObject deleteJSON = new JSONObject();
        try {
            deleteJSON.put("key", Common.API_KEY);
            deleteJSON.put("username", rider.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest deleteHttpRequestForJSON = new JsonObjectRequest(Request.Method.DELETE
                , (Common.BASE_URL + Common.RIDER_URL), deleteJSON
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
        deleteHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(deleteHttpRequestForJSON);

    }

    public static String errorMessages(String msgCode) {

        switch (msgCode) {

            case "API_KEY_REQUIRED":
                return "Contact developer with error: API_KEY_REQUIRED on sumitranjan52@gmail.com";

            case "API_KEY_INVALID":
                return "Contact developer with error: API_KEY_INVALID on sumitranjan52@gmail.com";

            case "INVALID_INPUT":
                return "Contact developer with error: INVALID_INPUT on sumitranjan52@gmail.com";

            case "LOGGED_IN":
                return "Signed in successfully!";

            case "USERNAME_OR_PASSWORD_INVALID":
                return "Username or password is invalid! Try again with correct credentials.";

            case "REGISTERED":
                return "You are registered successfully!";

            case "REGISTRATION_FAILED":
                return "We are sorry. Registration failed!";

            case "EMPTY_NAME":
                return "Please enter your good name";

            case "EMPTY_USERNAME":
                return "Please create your username";

            case "EMPTY_EMAIL":
                return "Please enter your email";

            case "EMPTY_PASSWORD":
                return "Please enter your password";

            case "EMPTY_PHONE":
                return "Please enter your mobile number";

            case "EMPTY_DL":
                return "Please enter your driving licence number";

            case "EMPTY_VEHICLE_NUMBER":
                return "Please enter your vehicle number";

            case "INVALID_EMAIL":
                return "Please enter a valid email (abc@xyz.com)";

            case "USERNAME_ALREADY_REGISTERED":
                return "It is already registered. Try login.";

            case "EMAIL_ALREADY_REGISTERED":
                return "It is already registered. Try login.";

            case "PHONE_ALREADY_REGISTERED":
                return "It is already registered. Try login.";

            case "DL_ALREADY_REGISTERED":
                return "It is already registered. Try login.";

            case "VEHICLE_NUMBER_ALREADY_REGISTERED":
                return "It is already registered. Try login.";

            case "RESET_SENT":
                return "Reset instruction is sent to the registered email Id.";

            case "RESET_NOT_SENT":
                return "Error in sending reset instruction.";

            case "USERNAME_NOT_EXIST":
                return "User doesn't exists with the entered username";

            case "EMPTY_USERID":
                return "Shared Preferences is altered. Try signing in again.";

            case "USER_NOT_EXIST":
                return "Shared Preferences is altered. Try signing in again.";

            case "UPDATED":
                return "We changed you data according to you.";

            case "NOTHING_CHANGED":
                return "Nothing Changed in Database.";

            case "DELETED":
                return "User deleted Successfully!";

            default:
                return "Something went wrong";

        }

    }
}
