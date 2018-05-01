# IOT_PROJECT
## Classes in Source Folder
1. IBMReceiver and IndicoREceiver class:
* This classes inherit BroadcastReceiver class 
* Needed when the app is in background and the background service is needed to run
2. IBMReceiverBoot and IndicoREceiverBoot:
* When the mobile device boot up, the background service automatically starts if the app is previously installed
3. Service_Analyze_IBM and Service_Analyze_Indico
* These are the two background services
* Both of the two services collect data from messages app, data written by keyboard app
* Background services collect these data twice a day
* Both the background services connect to the remote services to get emotion
* They also send the emotion to a remote realtime database hosted in Firebase
4. MainActivity
* MainActivity set the interval timer using AlarmManager
* Time is set to 12 hours, so that the services can run in a 12 hours interval.
5. Installation
* Installation is easy. Build the apk and run
* In first run message read permission should be granted for reading messages of the default app
