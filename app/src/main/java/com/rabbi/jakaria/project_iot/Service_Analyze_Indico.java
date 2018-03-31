package com.rabbi.jakaria.project_iot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;
import io.indico.Indico;
import io.indico.api.text.Emotion;
import java.util.HashMap;

public class Service_Analyze_Indico extends Service {
    public Service_Analyze_Indico() {
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
        Toast.makeText(this, "Indico Service Started", Toast.LENGTH_LONG).show();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Map params = new HashMap();
                    params.put("threshold", 0.25);

                    // single example
                    Indico indico = new Indico("8f174d44f34af1e6b6255a9011e1f474");
                    Map<Emotion, Double> results = indico.emotion.predict("I did it. " +
                            "I got into Grad School. Not just any program, but a GREAT program. :-)"
                            , params).getEmotion();

                    System.out.println(results);

                    // batch example
                    String[] example = {
                            "I did it. I got into Grad School. Not just any program, " +
                                    "but a GREAT program. :-)",
                            "Like seriously my life is bleak, " +
                                    "I have been unemployed for almost a year."
                    };
                    List<Map<Emotion, Double>> batchResults = indico.emotion.predict(example
                            ,params).getEmotion();
                    System.out.println(batchResults);

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
        Toast.makeText(this, "Indico Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
