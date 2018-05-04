package com.rabbi.jakaria.project_iot;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import io.indico.Indico;
import io.indico.api.text.Emotion;
import java.util.HashMap;
import android.provider.Settings.Secure;

// Service for getting text data, send to Indico service, get emotion and send to Firebase


public class Service_Analyze_Indico extends Service {

    String filter = "";
    Date Start1,End1;
    String text;
    boolean bool;
    String theString;

    public class LocalBinder1 extends Binder {
        public Service_Analyze_Indico getService(){
            return  Service_Analyze_Indico.this;
        }
    }

    public Service_Analyze_Indico() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public String getData()
    {
        String packageName = "com.blackcj.customkeyboard";
        String filePath;
        FileInputStream fis;

        try {
            PackageManager packageManager = getPackageManager();

            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            filePath = appInfo.dataDir + "/files/data.txt";

            System.out.println(filePath);

            try {

                fis = new FileInputStream(new File(filePath));
                theString = IOUtils.toString(fis, "UTF_8");
                // remove all contents
                //FileOutputStream writer = new FileOutputStream(filePath);

                Log.d("theString", "theStringINDICO: " + theString);
                //write locally
                //File path = getApplicationContext().getFilesDir();
                //File file = new File(path, "keyboardstring.txt");

                //FileOutputStream stream = new FileOutputStream(file);
                //try {
                //stream.write(theString.getBytes());
                //} finally {
                //stream.close();
                //}

            } catch (IOException e) {
                System.out.println("File not found: " + e.getMessage());
            }

        }catch (PackageManager.NameNotFoundException e) {

        }

        Log.d("theString", "theString: " + theString);
        return theString;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Indico Service Started", Toast.LENGTH_LONG).show();
        System.out.println("Indico Service Started");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    String msgData = "";

                    // Text message///////////////////////
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");


                    Date today = Calendar.getInstance().getTime();
                    String startDate = formatter1.format(today);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                    bool =  preferences.getBoolean("key", true);
                    System.out.println(bool);
                    // Now create a start and end time for this date in order to setup the filter.
                    if(bool == true) {

                        Start1 = formatter.parse(startDate + "T00:00:00");
                        End1 = formatter.parse(startDate + "T23:59:59");
                        bool = false;

                        SharedPreferences preferences1 = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences1.edit();
                        editor.putBoolean("key", bool);
                        editor.commit();
                    }
                    else
                    {
                        Start1 = formatter.parse(startDate + "T12:00:00");
                        End1 = formatter.parse(startDate + "T23:59:59");
                        bool = true;

                        SharedPreferences preferences2 = getApplicationContext().getSharedPreferences("SHARED", android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences2.edit();
                        editor.putBoolean("key", bool);
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


                    theString = getData();

                    System.out.println(msgData);
                    System.out.println(filter);

                    text = text+"."+theString;

                    System.out.println(text);

                    ////////////////////////////////////////////////////////////
                    Map params = new HashMap();
                    params.put("threshold", 0.25);

                    // single example
                    Indico indico = new Indico("8f174d44f34af1e6b6255a9011e1f474");
                    Map<Emotion, Double> results = indico.emotion.predict(text, params).getEmotion();

                    System.out.println(results);

                    Map.Entry<Emotion, Double> maxEntry = null;

                    for (Map.Entry<Emotion, Double> entry : results.entrySet())
                    {
                        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        {
                            maxEntry = entry;
                        }
                    }

                    System.out.println(maxEntry.getKey());

                    String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                            Secure.ANDROID_ID);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message_INDICO"+android_id);

                    myRef.child("Emotion_Indico").push().setValue(maxEntry.getKey());
                    myRef.child("Time_Indico").push().setValue(startDate);

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
        Toast.makeText(this, "Indico Service Destroyed", Toast.LENGTH_LONG).show();
        System.out.println("Indico Service Destroyed");
    }
}
