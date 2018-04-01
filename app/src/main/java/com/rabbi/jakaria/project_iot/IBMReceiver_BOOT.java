package com.rabbi.jakaria.project_iot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by root on 31/03/18.
 */

public class IBMReceiver_BOOT extends BroadcastReceiver {

    @Override
    public void onReceive(Context rcontext, Intent intent)
    {
        Log.e("Broadcast", "inside on receive of IBMReceiver_BOOT");
        Intent service1 = new Intent(rcontext, Service_Analyze_IBM.class);
        rcontext.startService(service1);

    }
}
