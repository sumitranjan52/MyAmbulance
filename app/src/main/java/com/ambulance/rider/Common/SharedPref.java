/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.rider.Common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sumit on 01-Apr-18.
 */

public class SharedPref {

    private Context context;
    private String prefName;
    private SharedPreferences preferences;

    public SharedPref(Context context, String prefName) {
        this.context = context;
        this.prefName = prefName;
        preferences = this.context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
    }
}
