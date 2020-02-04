# CallBlockerAndroid


### Testing

Detailed info can be found on: [Esspresso Documentation](https://developer.android.com/training/testing/espresso)

Dependencies needed for the implementation:

```
androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
androidTestImplementation 'androidx.test:runner:1.2.0'
androidTestImplementation 'androidx.test:rules:1.2.0'
androidTestImplementation 'androidx.test.espresso:espresso-intents:3.1.0'
```

This needs to be added inside android.defaultConfig
    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
```    
upgrade to androidX to be compactible with the libraries required by firebase. This also made
project up to date with latest technologies and practices used.
```
     
All testing .java files are inside and should be inside 
```
src/androidTest/java/co.teltech.callblocker
````

# Firebase + Bitrise Implementation

Explained in the steps below is the complete integration of the Firebase with the Bitrise

## Firebase implementation

Detailed info can be found on: [Firebase Documentaiton](https://firebase.google.com/docs/android/setup)

APK or ABB and Test APK needs to be created to create Instrumented test inside Test lab. Those can
be uploaded trough web UI.

From there, virtual or real device can be selected. 

## Bitrise

Adding app to the Bitrise is pretty straightforward through the Bitrise UI

Link to site: 

[bitrise.io](https://www.bitrise.io/)

## Bitrise + Firebase Test Lab

To implement Firebase tests te be done when Bitrise build is finished, few steps are needed.

You need to install Firebase CLI, you could need it later in the process:
[Firebase CLI](https://firebase.google.com/docs/cli/#install-cli-mac-linux)

## Setting up needed Google Cloud Platform permissions

API Access needs to be added from Google Cloud Platform APIs for implementation to work.
Go to the project: https://console.cloud.google.com/projectselector2/apis/library?supportedpurview=project
Select needed project, and search and add following API access to the project:
-Google Cloud Testing API 
-Cloud Tool Results API

## Setting up needed variables

Go to the "Code Signing" tab.
You will need a Google Cloud Platform service account and its JSON key file.
You can learn more about those on this link: 

[Google service accounts docs](https://cloud.google.com/compute/docs/access/service-accounts)

Needed JSON key file details can be found on this link: 

[Google service account keys](https://cloud.google.com/iam/docs/creating-managing-service-account-keys#iam-service-account-keys-create-console)

To get needed JSON key file, go to the 
[Google projects](https://console.cloud.google.com/project/_/iam-admin).
After that, select project that you need access.

On the navigation bar on the left side select "Service Accounts" tab.

If there is no profile present, create one with "Editor" role. UI will guide you through the process.
At the last step, you press on "Optional" create key. After that you will be presented with option to download
created key. Download it in the JSON format.

If you already have profile, go to the three dot options menu, click on edit and press on the "Create key".
After that you will be presented with option to download created key. Download it in the JSON format.

Return to the Bitrise and "Code Signing" tab.
Upload your service account’s JSON key file on Code Signing tab, under Generic File Storage: name it 
SERVICE_ACCOUNT_KEY and drop the file into the box, so the downloadable environment variable that
contains the url will be: $BITRISEIO_SERVICE_ACCOUNT_KEY_URL

Go to the "Env Vars" tab, and add "GCP_PROJECT" variable.
This is to have Google Cloud Project key available.
Format of that key is like this: "appname-123456"
Key can be found on https://console.cloud.google.com/home/dashboard?project=<**token-id**> under Project info.
Replace **token-id** with your app id


## Setting up "Android Build for UI Testing" workflow step

Go to the Bitrise and open workflow for the Android project. After 
"Android Unit Test"
step, add new step named: "Android Build for UI Testing"

After that, return to the "Workflows tab" and go to the "Android Build for UI testing" step.
Variables setup:
"Project location"
-use default project location in most cases
"Module"
-if your app has more different modules, specify correct one to build. If not, use default "app"
"Variant"
-type of the build: "PROD", "DEV", "STAGING"

This workflow step creates $BITRISE_APK_PATH and $BITRISE_TEST_APK_PATH that we are going to send to the 
Firebase for testing

#Setting up "Script" workflow step

After you created "Android Build for UI Testing" workflow step, next step to add after previous one is
"Script" workflow step. After this step is added, this script is used for the sending previously created 
$BITRISE_APK_PATH and $BITRISE_TEST_APK_PATH to the Firebase.

After that is created, use this script:
```
 #!/bin/bash
            set -ex

            #Download service account key
            curl -o /tmp/sacc_key.json $BITRISEIO_SERVICE_ACCOUNT_KEY_URL
            #Activate cloud client with the service account
            gcloud auth activate-service-account -q --key-file /tmp/sacc_key.json
            #Set the project's id used on Google Cloud Platform
            gcloud config set project $GCP_PROJECT

            RESULT_DIR="build-$BITRISE_BUILD_NUMBER"

            APK="--app=$BITRISE_APK_PATH --test=$BITRISE_TEST_APK_PATH"

            TYPE="instrumentation"
            DEVICES="--device model=Nexus6,version=21,locale=en,orientation=portrait"

            gcloud firebase test android run $APK $DEVICES --type=$TYPE --results-dir=$RESULT_DIR/ >>$BITRISE_DEPLOY_DIR/gcloudlog.log 2>&1

            SRCPTH=$BITRISE_DEPLOY_DIR/test_results
            EXPPTH=$BITRISE_DEPLOY_DIR
            mkdir $SRCPTH

            #Download test results

            FB_BUCKET=$(grep -oP "(?<=browser\/).*(?<=\/$RESULT_DIR)" $BITRISE_DEPLOY_DIR/gcloudlog.log)

            gsutil -m cp -R gs://$FB_BUCKET $SRCPTH

            for file in $(find $SRCPTH -type f)
            do
                if [[ -f $file ]]; then
                   f=$(echo ${file//$SRCPTH\/$RESULT_DIR\/})
                   mv $file $(echo $EXPPTH/${f//\//_})
                fi
            done 
```
 This script sends builds to Firebase, and after tests are performed, downloads test results, that are shown inside 
 the "APPS & ARTIFACTS" tab


## End info

After this steps are completed, create test build on Bitrise. When build is finished,
go to the "APSS & ARTIFACTS", and there you should have test results from the Firebase.










    
    
    
    
    

