package com.ambulance.rider;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ambulance.rider.Backend.Accounts;
import com.ambulance.rider.Common.AlertDialogBox;
import com.ambulance.rider.Common.NetworkErrorMessages;
import com.ambulance.rider.Interfaces.AmbulanceDialogInterface;
import com.ambulance.rider.Interfaces.VolleyJSONResponses;
import com.ambulance.rider.Model.Rider;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private Button btnForgot;
    private EditText usernameForgot;
    private ScrollView rootForgotPasswordView;

    private Context mContext;

    private AlertDialog dialog;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        btnForgot = view.findViewById(R.id.btnForgot);
        usernameForgot = view.findViewById(R.id.usernameForgot);
        rootForgotPasswordView = view.findViewById(R.id.rootForgotPasswordView);
        usernameForgot.requestFocus();

        btnForgot.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        if (v.getId() == R.id.btnForgot){
            dialogBuilder("","Please wait....",false);
            fnForgot();
            usernameForgot.setText("");
        }
    }

    private void fnForgot() {

        String strUsername;
        strUsername = usernameForgot.getText().toString();

        if (TextUtils.isEmpty(strUsername)) {

            dialog.dismiss();
            dialogBuilder("Notice!", "Complete all fields and try again.", true);

        } else {

            Rider rider = new Rider();
            rider.setUsername(strUsername);

            final Accounts oldDriver = new Accounts(mContext, rider);
            oldDriver.resetPassword(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("fmasfnkas", "onSuccess: "+response);
                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            dialogBuilder("Congratulations!", oldDriver.errorMessages(response.getString("response")), true);

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_USERNAME")) {

                                    usernameForgot.setError(oldDriver.errorMessages(jsonArray.getString(i)));

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
