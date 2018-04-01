package com.rabbi.jakaria.project_iot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by root on 31/03/18.
 */

public class IBMReceiverBoot extends BroadcastReceiver {

    public static final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 20000;

    @Override
    public void onReceive(Context rcontext, Intent intent)
    {
        Log.e("Broadcast", "inside on receive of IBMReceiverBoot");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmManager = (AlarmManager) rcontext.getSystemService(Context.ALARM_SERVICE);

            Intent myIntent = new Intent(rcontext, Service_Analyze_IBM.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(rcontext,
                    0, myIntent, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ALARM_TRIGGER_AT_TIME,
                    1000 * 30 ,pendingIntent);

        }

    }
}
