package com.ambulance.rider;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambulance.rider.Common.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment implements View.OnClickListener {

    private ImageView logo;
    private Button btnFindBooking;
    private TextView status;

    private BroadcastReceiver broadcastReceiver;

    private Context mContext;
    private Activity activity;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("SPLASH", "onCreate: Fragment Splash");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        logo = view.findViewById(R.id.logo);
        btnFindBooking = view.findViewById(R.id.btnFindBooking);
        status = view.findViewById(R.id.status);

        if (Common.isLoggedIn){
            logo.setPadding(0,0,0,100);
            status.setText("Getting your location");
            status.setTextSize(20.0f);
            btnFindBooking.setVisibility(View.GONE);
        }else{
            logo.setPadding(0,0,0,0);
            status.setText("Ambulance");
            btnFindBooking.setVisibility(View.VISIBLE);
            btnFindBooking.setOnClickListener(this);
        }

        return view;
    }

    /* implement click on views */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFindBooking:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                ,android.R.anim.slide_out_right
                                ,android.R.anim.slide_in_left
                                ,android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container,new LoginFragment())
                        .commit();
                break;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isLoggedIn){
            if (broadcastReceiver == null) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("BROADCAST", "fired");
                        try {
                            Common.latitude = intent.getExtras().getDouble("lat");
                            Common.longitude = intent.getExtras().getDouble("lng");
                            startActivity(new Intent(mContext,MainActivity.class));
                            activity.finish();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
            mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }
}
