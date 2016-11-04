package com.michaelflisar.lumberjack.demo;

import com.michaelflisar.lumberjack.FileLoggingSetup;
import com.michaelflisar.lumberjack.FileLoggingTree;
import com.michaelflisar.lumberjack.FileLoggingUtil;
import com.michaelflisar.lumberjack.NotificationLoggingSetup;
import com.michaelflisar.lumberjack.NotificationLoggingTree;
import com.michaelflisar.lumberjack.formatter.DefaultLogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;
import com.michaelflisar.lumberjack.formatter.ILogGroup;

import java.util.ArrayList;

import timber.log.DebugTree;
import timber.log.Timber;

/**
 * Created by Michael on 03.11.2016.
 */

public class L extends com.michaelflisar.lumberjack.L {

    // -----------------------------
    // Basic minimal setup
    // -----------------------------

    // private init function, this would be enough to start, but we use initLumberjack
    // for an advanced initialisation
    private static void initTimber()
    {
        // create your trees and plant them in timber
        Timber.plant(new DebugTree(true));
        Timber.plant(new FileLoggingTree(true, FILE_LOG_SETUP));
        Timber.plant(new NotificationLoggingTree(MainApp.get(), true, NOTIFICATION_LOG_SETUP));

        // setup a log formatter, you can provide a custom one if you want to
        setLogFormatter(new DefaultLogFormatter(5, true, true));
    }

    // -----------------------------
    // Advanced setup
    // -----------------------------

    public static void initLumberjack()
    {
        // base setup
        initTimber();

        // register a formatter for a class and define, HOW this class should be logged (as a simple value and as a value in a collection)
        registerFormatter(MainActivity.TestClass.class, new ILogClassFormatter<MainActivity.TestClass>() {
            @Override
            public String log(MainActivity.TestClass item, boolean logInList) {
                return customFormat("TestClass", item.getLogData(), logInList);
            }
        });

        // some test logs...
        L.d(G_TEST, "initLumberjack fertig");
        L.d(G_TEST, "LogFiles: %s", FileLoggingUtil.getAllExistingLogFiles(FILE_LOG_SETUP));
    }

    // -----------------------------
    // Advanced - Groups
    // -----------------------------

    // Groups
    public static final ILogGroup G_TEST = new ILogGroup() {
        @Override
        public String getTag() {
            return "TEST-GROUP";
        }
    };

    public static final ArrayList<ILogGroup> LOG_GROUPS = new ArrayList<ILogGroup>() {
        {
            add(G_TEST);
        }
    };

    // -----------------------------
    // Advanced - File Logger
    // -----------------------------

    private static final FileLoggingSetup FILE_LOG_SETUP = new FileLoggingSetup(MainApp.get());

    // -----------------------------
    // Advanced - Notification Logger
    // -----------------------------

    private static final NotificationLoggingSetup NOTIFICATION_LOG_SETUP = new NotificationLoggingSetup(R.drawable.abc_ic_clear_material)
            .withTitle("Demo Logger")
            //.withBigIcon(R.mipmap.ic_launcher_default)
            .withNotificationId(150)
            .withFilters(LOG_GROUPS);

    // -----------------------------
    // Format helper function
    // -----------------------------

    private static String customFormat(String type, String value, boolean logInList) {
        if (!logInList)
            return type + ": [" + value + "]";
        return value;
    }
}