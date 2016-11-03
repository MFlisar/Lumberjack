package com.michaelflisar.lumberjack.demo;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by Michael on 03.11.2016.
 */

public class MainApp extends Application {

    private static MainApp mInstance = null;

    public void onCreate()
    {
        super.onCreate();
        mInstance = this;

        L.initLumberjack();
    }

    public static MainApp get()
    {
        return mInstance;
    }
}
