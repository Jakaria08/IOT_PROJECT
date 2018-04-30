package com.rabbi.jakaria.project_iot;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.DocumentAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneInput;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import static android.os.SystemClock.sleep;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;



public class Service_Analyze_IBM extends Service {

    final ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2018-03-27");
    int REQUEST_CODE_ASK_PERMISSIONS = 123;

    String username;
    String password;
    String filter = "";
    Date Start1,End1;
    String text;
    boolean bool;

    Firebase firebase;



    protected Handler handler;

    public class LocalBinder extends Binder{
        public Service_Analyze_IBM getService(){
            return  Service_Analyze_IBM.this;
        }
    }

    public Service_Analyze_IBM() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "IBM Service Started", Toast.LENGTH_LONG).show();
        System.out.println("IBM Service Started");

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
                    String msgData = "";

                // Text message///////////////////////
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");


                    Date today = Calendar.getInstance().getTime();
                    String startDate = formatter1.format(today);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                    bool =  preferences.getBoolean("key1", true);
                    System.out.println(bool);
                    // Now create a start and end time for this date in order to setup the filter.
                    if(bool == true) {

                        Start1 = formatter.parse(startDate + "T00:00:00");
                        End1 = formatter.parse(startDate + "T23:59:59");
                        bool = false;

                        SharedPreferences preferences1 = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences1.edit();
                        editor.putBoolean("key1", bool);
                        editor.commit();
                    }
                    else
                    {
                        Start1 = formatter.parse(startDate + "T12:00:00");
                        End1 = formatter.parse(startDate + "T23:59:59");
                        bool = true;

                        SharedPreferences preferences2 = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences2.edit();
                        editor.putBoolean("key1", bool);
                        editor.commit();
                        System.out.println(bool);
                    }
                    // Now create the filter and query the messages.
                    filter = "date>=" + Start1.getTime() + " and date<=" + End1.getTime();

            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/sent"),
                    null, filter, null, null);


            if (cursor.moveToFirst()) { // must check the result to prevent exception
                    //String msgData = "";
                    for (int idx = 0; idx < cursor.getCount(); idx++) {
                        msgData += " " + cursor.getString(cursor.getColumnIndexOrThrow("body"));
                        cursor.moveToNext();
                    }
                    text = msgData;
                    // use msgData
            } else {
                // empty box, no SMS
            }


        System.out.println(msgData);
            System.out.println(filter);

                ///// Emotion from Server ////////////////

                //try

                //{
                    //try {
                        //JSONObject tones = new JSONObject(IOUtils.toString(getResources()
                                //.openRawResource(R.raw.tonechat), "UTF-8")); // Convert the file into a JSON object

                        ToneInput toneInput = new ToneInput.Builder()
                                .text(text).build();
                        ToneOptions options = new ToneOptions.Builder()
                                .toneInput(toneInput).build();
                        ToneAnalysis tone = toneAnalyzer.tone(options).execute();
                        //System.out.println(tone);
                        DocumentAnalysis tone1 = tone.getDocumentTone();
                        String tone2 = tone1.getTones().get(0).getToneName();
                        //System.out.println(tone);

                        System.out.println(tone2);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message_IBM");

                    myRef.child("Emotion").push().setValue(tone2);
                    myRef.child("Emotion").push().setValue(today);

                    //} catch (IOException e) {
                        //System.out.println(e.getMessage());
                    //}

                //} catch (JSONException e) {
                    //System.out.println(e.getMessage());
                //}

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "IBM Service Destroyed", Toast.LENGTH_LONG).show();
        System.out.println("IBM Service Destroyed");
    }
}
