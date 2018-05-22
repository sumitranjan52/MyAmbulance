package com.ambulance.rider;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ambulance.rider.Backend.Accounts;
import com.ambulance.rider.Backend.FCMToken;
import com.ambulance.rider.Common.AlertDialogBox;
import com.ambulance.rider.Common.Common;
import com.ambulance.rider.Common.NetworkErrorMessages;
import com.ambulance.rider.Interfaces.AmbulanceDialogInterface;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.ambulance.rider.Model.Rider;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView forgotLink, createAccountLink;
    private Button btnLogin;
    private EditText username, password;
    private ScrollView rootLoginView;

    private Context mContext;
    private Activity activity;

    private AlertDialog dialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        username = view.findViewById(R.id.usernameLogin);
        password = view.findViewById(R.id.passwordLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        forgotLink = view.findViewById(R.id.forgetLink);
        createAccountLink = view.findViewById(R.id.createAccountLink);
        rootLoginView = view.findViewById(R.id.rootLoginView);
        username.requestFocus();

        btnLogin.setOnClickListener(this);
        createAccountLink.setOnClickListener(this);
        forgotLink.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    public void dialogBuilder(String title, String msg, boolean cancellable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(cancellable);
        builder.setTitle(title);
        builder.setMessage(msg);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                dialogBuilder("", "Signing in. Please wait...", false);
                fnLogin();
                break;

            case R.id.createAccountLink:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
                break;

            case R.id.forgetLink:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container, new ForgotPasswordFragment())
                        .commit();
                break;
        }
    }

    private void fnLogin() {

        String strUsername, strPassword;
        strUsername = username.getText().toString();
        strPassword = password.getText().toString();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strPassword)) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Complete all fields and try again.", true);

        } else if (strPassword.length() < 8) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Password should be minimum of 8 characters.", true);

        } else {

            Rider rider = new Rider();
            rider.setUsername(strUsername);
            rider.setPassword(strPassword);

            final Accounts oldDriver = new Accounts(mContext, rider);
            oldDriver.loginRider(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {

                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("account", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", response.getJSONObject("response").getJSONObject("response").getString("_id"));
                            editor.putBoolean("auto-login", true);
                            editor.apply();

                            String userId = mContext.getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
                            String token = mContext.getSharedPreferences("firebase",MODE_PRIVATE).getString("fcmToken","");
                            if (!userId.equals("")){

                                new FCMToken(mContext,userId,token).sendTokenToServer(new VolleyJSONResponses() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("FCM_RESPONSE",response.toString());
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        Log.d("FCM_RESPONSE",error.toString());
                                    }
                                });
                            }

                            dialogBuilder("Congratulations!", oldDriver.errorMessages(response.getJSONObject("response").getString("status")), true);
                            Common.isLoggedIn = true;
                            dialog.dismiss();
                            startActivity(new Intent(mContext,MainActivity.class));
                            activity.finish();

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_USERNAME")) {

                                    username.setError(oldDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PASSWORD")) {

                                    password.setError(oldDriver.errorMessages(jsonArray.getString(i)));

                                }

                            }

                        } else if (response.has("invalid")) {

                            dialogBuilder("Something not right!", oldDriver.errorMessages(response.getString("invalid")), true);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(VolleyError error) {
                    dialog.dismiss();
                    Log.d("LOGIN",error.toString());
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
            });
        }
    }
}
