# IOT_PROJECT
## Link to the associated Projects
1. https://github.com/Jakaria08/AndroidCustomKeyboard
2. https://github.com/Jakaria08/emotion-analysis
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
* In first run, message read permission should be granted for reading messages of the default app


## Overall System Architecture of the Project
![1](https://user-images.githubusercontent.com/7825643/39469285-ac8dd8ee-4cf4-11e8-9d69-a41bac89f09b.png)

## Modules in the Project
![2](https://user-images.githubusercontent.com/7825643/39469332-e8180c54-4cf4-11e8-9970-2f9737534c84.png)

## Running App
![3](https://user-images.githubusercontent.com/7825643/39469397-36d7d19e-4cf5-11e8-9a6b-22c2ab3d3b40.png)

## Reading data from keyboard app
![4](https://user-images.githubusercontent.com/7825643/39469426-5dbc2b16-4cf5-11e8-9de0-773cfe548088.png)
