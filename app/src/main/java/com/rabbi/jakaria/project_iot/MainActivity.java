package com.rabbi.jakaria.project_iot;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS=123;
    public static final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 20000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.button);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                get_permission();

            }
        });
    }

    private void get_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.READ_SMS))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS},REQUEST_CODE_ASK_PERMISSIONS );

            }
        } else {
//            Intent intent = new Intent(MainActivity.this,
//                    Service_Analyze_IBM.class);
//            Intent intent1 = new Intent(MainActivity.this,
//                    Service_Analyze_Indico.class);
//            startService(intent);
//            startService(intent1);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent myIntent = new Intent(this, IBMReceiver.class);
            Intent myIntent1 = new Intent(this, IndicoReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    0, myIntent, 0);

            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,
                    1, myIntent1, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ALARM_TRIGGER_AT_TIME,
                    1000 * 30 ,pendingIntent);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ALARM_TRIGGER_AT_TIME,
                    1000 * 30 ,pendingIntent1);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Intent intent = new Intent(MainActivity.this,
//                            Service_Analyze_IBM.class);
//                    Intent intent1 = new Intent(MainActivity.this,
//                            Service_Analyze_Indico.class);
//                    startService(intent);
//                    startService(intent1);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Intent myIntent = new Intent(this, IBMReceiver.class);
                    Intent myIntent1 = new Intent(this, IndicoReceiver.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                            0, myIntent, 0);

                    PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,
                            1, myIntent1, 0);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ALARM_TRIGGER_AT_TIME,
                            1000 * 30 ,pendingIntent);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,ALARM_TRIGGER_AT_TIME,
                            1000 * 30 ,pendingIntent1);

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission",
                            Toast.LENGTH_LONG).show();

                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
