package com.humansapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by jordan on 2014-08-14.
 */
public class HumansApplication extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        HumansApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return HumansApplication.context;
    }
}
