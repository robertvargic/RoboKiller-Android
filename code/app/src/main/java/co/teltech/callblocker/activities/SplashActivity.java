package co.teltech.callblocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import co.teltech.callblocker.R;

/**
 * Created by tomislavtusek on 16/08/2018.
 */

public class SplashActivity extends Activity {

    private Thread mSplashThread;
    private int mSplashDuration = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(mSplashDuration);
                    }
                }
                catch(InterruptedException ex){
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mSplashThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        if(evt.getAction() == MotionEvent.ACTION_DOWN){
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }

}
