package com.rabbi.jakaria.project_iot;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneInput;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import static android.os.SystemClock.sleep;


public class Service_Analyze_IBM extends Service {

    final ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2018-03-27");
    int REQUEST_CODE_ASK_PERMISSIONS = 123;
    String msgData = "";
    String username;
    String password;
    protected Handler handler;

    public Service_Analyze_IBM() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "IBM Service Started", Toast.LENGTH_LONG).show();
        //sleep(5000);
        try {

            try {
                JSONObject credentials = new JSONObject(IOUtils.toString(getResources()
                        .openRawResource(R.raw.credentials), "UTF-8")); // Convert the file into a JSON object
                // Extract the two values
                username = credentials.getString("username");
                password = credentials.getString("password");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        Log.d("username", "username: " + username);

        toneAnalyzer.setUsernameAndPassword(username, password);

        Log.d("username", "username: " + username);

        ///////////////////////////////////////////////////////////////

        //handler = new Handler();
        //handler.post(new Runnable() {
            //@Override
           // public void run() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                // Text message///////////////////////

            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/sent"),
                    null, null, null, null);


            if (cursor.moveToFirst()) { // must check the result to prevent exception
                    //String msgData = "";
                    for (int idx = 0; idx < cursor.getCount(); idx++) {
                        msgData += " " + cursor.getString(cursor.getColumnIndexOrThrow("body"));
                        cursor.moveToNext();
                    }
                    // use msgData
            } else {
                // empty box, no SMS
            }


        System.out.println(msgData);

                ///// Emotion from Server ////////////////

                try

                {
                    try {
                        JSONObject tones = new JSONObject(IOUtils.toString(getResources()
                                .openRawResource(R.raw.tonechat), "UTF-8")); // Convert the file into a JSON object

                        ToneInput toneInput = new ToneInput.Builder()
                                .text(tones.getString("text")).build();
                        ToneOptions options = new ToneOptions.Builder()
                                .toneInput(toneInput).build();
                        ToneAnalysis tone = toneAnalyzer.tone(options).execute();
                        System.out.println(tone);


                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "IBM Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
