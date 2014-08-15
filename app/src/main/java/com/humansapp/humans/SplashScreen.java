package com.humansapp.humans;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

/**
 * Created by jordan on 2014-08-12.
 */
public class SplashScreen extends Activity {

    // Timer
    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
