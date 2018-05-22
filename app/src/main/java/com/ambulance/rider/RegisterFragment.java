package com.ambulance.rider;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

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
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText name, username, email, phone, password;
    private ScrollView rootRegisterView;
    private Button btnCreate;
    private Spinner bGroup;

    private String strBloodGroup;

    private Context mContext;
    private Activity activity;

    private AlertDialog dialog;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        name = view.findViewById(R.id.txtName);
        username = view.findViewById(R.id.usernameRegister);
        email = view.findViewById(R.id.emailRegister);
        phone = view.findViewById(R.id.phoneRegister);
        password = view.findViewById(R.id.passwordRegister);
        bGroup = view.findViewById(R.id.bloodGroup);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,R.array.blood_group,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bGroup.setAdapter(adapter);
        bGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBloodGroup = parent.getItemAtPosition(position).toString();
                Log.d("TAG",parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rootRegisterView = view.findViewById(R.id.rootRegisterView);
        btnCreate = view.findViewById(R.id.btnCreate);
        name.requestFocus();
        btnCreate.setOnClickListener(this);
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
        if (v.getId() == R.id.btnCreate) {
            dialogBuilder("", "Registering. Please wait...", false);
            fnCreateANewAccount();
        }
    }

    private void fnCreateANewAccount() {

        String strName;
        String strUsername;
        String strEmail;
        String strPhone;
        String strPassword;
        strName = this.name.getText().toString();
        strUsername = this.username.getText().toString();
        strEmail = this.email.getText().toString();
        strPhone = this.phone.getText().toString();
        strPassword = this.password.getText().toString();

        /* Validation */
        if (TextUtils.isEmpty(strName) || TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strEmail)
                || TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strPassword) || TextUtils.isEmpty(strBloodGroup)) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Complete all fields and try again.", true);

        } else if (strPhone.length() < 10 || strPhone.length() > 10) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Mobile number should be 10 digit long. It should not contain country code. Default is India (+91)", true);

        } else if (strPassword.length() < 8) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Password should be minimum of 8 characters.", true);

        } else {

            Rider rider = new Rider();
            rider.setName(strName);
            rider.setUsername(strUsername);
            rider.setEmail(strEmail);
            rider.setPhone(strPhone);
            rider.setPassword(strPassword);
            rider.setBloodGroup(strBloodGroup);

            final Accounts newDriver = new Accounts(mContext, rider);
            newDriver.registerRider(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", response.getJSONObject("response").getString("id"));
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
                                        error.printStackTrace();
                                    }
                                });

                            }

                            dialogBuilder("Congratulations!", newDriver.errorMessages(response.getJSONObject("response").getString("status")), true);
                            Common.isLoggedIn = true;
                            dialog.dismiss();
                            startActivity(new Intent(mContext,MainActivity.class));
                            activity.finish();

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_NAME")) {

                                    name.setError(newDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_USERNAME") || jsonArray.get(i).equals("USERNAME_ALREADY_REGISTERED")) {

                                    username.setError(newDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_EMAIL") || jsonArray.get(i).equals("INVALID_EMAIL") || jsonArray.get(i).equals("EMAIL_ALREADY_REGISTERED")) {

                                    email.setError(newDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PASSWORD")) {

                                    password.setError(newDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PHONE") || jsonArray.get(i).equals("PHONE_ALREADY_REGISTERED")) {

                                    phone.setError(newDriver.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_BLOOD_GROUP")) {

                                    Toast.makeText(mContext,"Blood group was not selected",Toast.LENGTH_SHORT).show();

                                }

                            }

                        } else if (response.has("invalid")) {

                            dialogBuilder("Something not right!", newDriver.errorMessages(response.getString("invalid")), true);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    dialog.dismiss();
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
