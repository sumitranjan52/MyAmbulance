/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.rider.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ambulance.rider.Interfaces.NetworkChangeInterface;

/**
 * Created by sumit on 09-Apr-18.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    NetworkChangeInterface networkChangeInterface;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (networkChangeInterface != null) {
            networkChangeInterface.onNetworkChange(isConnected);
        }
    }
}
