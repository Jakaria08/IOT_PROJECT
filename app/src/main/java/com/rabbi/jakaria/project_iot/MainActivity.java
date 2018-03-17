package com.rabbi.jakaria.project_iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.JsonElement;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneChatOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneInput;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Utterance;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.UtteranceAnalyses;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2018-03-17");
    String username;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            try {
                JSONObject credentials = new JSONObject(IOUtils.toString(getResources().openRawResource(R.raw.credentials), "UTF-8")); // Convert the file into a JSON object
                // Extract the two values
                username = credentials.getString("username");
                password = credentials.getString("password");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        toneAnalyzer.setUsernameAndPassword(username, password);
        Log.d("username", "username: " + username);

        //Thread thread = new Thread(new Runnable(){
        Thread thread = new Thread(new Runnable() {
            public void run() {

                try

                {
                    try {
                        JSONObject tones = new JSONObject(IOUtils.toString(getResources().openRawResource(R.raw.tonechat), "UTF-8")); // Convert the file into a JSON object

                        ToneInput toneInput = new ToneInput.Builder()
                                .text(tones.getString("text")).build();
                        ToneOptions options = new ToneOptions.Builder()
                                .toneInput(toneInput).build();
                        ToneAnalysis tone = toneAnalyzer.tone(options).execute();
                        System.out.println(tone);


                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                } catch (JSONException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        });

        thread.start();
    }
}
