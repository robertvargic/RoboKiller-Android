# CallBlockerAndroid


##Testing

Detailed info can be found on: https://developer.android.com/training/testing/espresso

Dependencies needed for the implementation:

    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    androidTestImplementation 'androidx.test:runner:1.2.0'
    androidTestImplementation 'androidx.test:rules:1.2.0'

This needs to be added inside android.defaultConfig
    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    
    upgrade to androidX to be compactible with the libraries required by firebase. This also made
    project up to date with latest technologies and practices used.
     
All testing .java files are inside and should be inside src/androidTest/java/co.teltech.callblocker

Firebase implementation

Detailed info can be found on: https://firebase.google.com/docs/android/setup


    
    
    
    
    

