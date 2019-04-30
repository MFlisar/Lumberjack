package com.michaelflisar.lumberjack.demo;

import android.app.Activity;
import android.content.Intent;

import com.michaelflisar.lumberjack.FileLoggingSetup;
import com.michaelflisar.lumberjack.FileLoggingTree;
import com.michaelflisar.lumberjack.NotificationLoggingSetup;
import com.michaelflisar.lumberjack.NotificationLoggingTree;
import com.michaelflisar.lumberjack.OverlayLoggerUtil;
import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.filter.ILogFilter;
import com.michaelflisar.lumberjack.formatter.DefaultLogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;

import java.util.ArrayList;

import timber.log.ConsoleTree;
import timber.log.Timber;

/**
 * Created by Michael on 03.11.2016.
 */

public class L extends com.michaelflisar.lumberjack.L {

    /*
    // -----------------------------
    // Basic minimal setup
    // -----------------------------

    // private init function, this would be enough to start, but we use initLumberjack
    // for an advanced initialisation
    private static void initTimber()
    {
        // OPTIONAL: Filter can be null as well, if you don't want to use the filter functions!
        // create your trees and plant them in timber
        Timber.plant(new ConsoleTree(true, true, TEST_FILTER));
        Timber.plant(new FileLoggingTree(true, FILE_LOG_SETUP, TEST_FILTER));
        Timber.plant(new NotificationLoggingTree(MainApp.get(), true, NOTIFICATION_LOG_SETUP, TEST_FILTER));
        // we can't init the overlay logger here, because we need an activity context for getting the draw overlay permission!

        // OPTIONAL: setup a log formatter, you can provide a custom one if you want to
        setLogFormatter(new DefaultLogFormatter(5, true, true));
    }

    // -----------------------------
    // Advanced setup
    // -----------------------------

    public static void initLumberjack()
    {
        // base setup
        initTimber();

        // OPTIONAL: register a formatter for a class and define, HOW this class should be logged (as a simple value and as a value in a collection)
        registerFormatter(MainActivity.TestClass.class, new ILogClassFormatter<MainActivity.TestClass>() {
            @Override
            public String log(MainActivity.TestClass item, boolean logInList) {
                return customFormat("TestClass", item.getLogData(), logInList);
            }
        });

        // some test logs...
        L.withGroup(G_TEST1).d("initLumberjack fertig");
        L.withGroup(G_TEST2).d("LogFiles: %s", FileLoggingUtil.getAllExistingLogFiles(FILE_LOG_SETUP));
    }

    public static void initOverlayLogger(Activity activity)
    {
        // the utility makes sure to only plant one instance of an overlay logger
        OverlayLoggerUtil.initOverlayLogger(activity, OVERLAY_LOG_SETUP, TEST_FILTER);
    }

    public static void handleOverlayPermissionDialogResult(int requestCode, int resultCode, Intent data)
    {
        // the utility method will hand on the result to the overlay tree, which then can reevaluate if
        // it has all necessary permissions and will the result
        Boolean success = OverlayLoggerUtil.handleOverlayPermissionDialogResult(requestCode, resultCode, data);

        L.d(G_TEST1, "Overlay permission granted: %b", success);
    }

    // -----------------------------
    // Advanced - Groups and filters
    // -----------------------------

    // Groups
    public static final String G_TEST1 = "TEST-GROUP 1";
    public static final String G_TEST2 = "TEST-GROUP 2";
    public static final String G_FILTERED = "FILTERED";

    // Filter - can be null as well, then no logs will be filtered!
    private static ILogFilter TEST_FILTER = new ILogFilter()
    {
        @Override
        public boolean valid(String group, int priority)
        {
            return group == null || !group.equals(G_FILTERED);
        }
    };

    // -----------------------------
    // Advanced - File Logger
    // -----------------------------

    private static final FileLoggingSetup FILE_LOG_SETUP = new FileLoggingSetup.Builder(MainApp.get())
            .build();

    // -----------------------------
    // Advanced - Notification Logger
    // -----------------------------

    private static final NotificationLoggingSetup NOTIFICATION_LOG_SETUP = new NotificationLoggingSetup(R.drawable.abc_ic_clear_material)
            .withTitle("Demo Logger")
            //.withBigIcon(R.mipmap.ic_launcher_default)
            .withNotificationId(150)
            .withButtonIntentRequestCodeBase(250);

    // -----------------------------
    // Advanced - Overlay Logger
    // -----------------------------

    private static final OverlayLoggingSetup OVERLAY_LOG_SETUP = new OverlayLoggingSetup()
            .withStartExpanded(false);

    // -----------------------------
    // Format helper function
    // -----------------------------

    private static String customFormat(String type, String value, boolean logInList) {
        if (!logInList)
            return type + ": [" + value + "]";
        return value;
    }
    */
}