package com.michaelflisar.lumberjack.demo;

import android.app.Application;

import com.michaelflisar.lumberjack.FileLoggingSetup;
import com.michaelflisar.lumberjack.FileLoggingTree;
import com.michaelflisar.lumberjack.demo.filelogging.FileLoggingSetupsUtil;

import timber.log.Timber;

/**
 * Created by Michael on 03.11.2016.
 */

public class MainApp extends Application {
    public void onCreate() {
        super.onCreate();
        FileLoggingSetup setup = FileLoggingSetupsUtil.getFileSizedLoggingSetup(this);

        Timber.plant(new FileLoggingTree(false, setup, null));
        Timber.plant(new Timber.DebugTree());
    }
}
