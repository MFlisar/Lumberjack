package com.michaelflisar.lumberjack;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.michaelflisar.lumberjack.formatter.ILogGroup;

import java.util.ArrayList;

/**
 * Created by Michael on 17.10.2016.
 */

public class NotificationLoggingSetup
{
    String mTitle = "Logger";
    int mBigIcon = -1;
    int mSmallIcon = -1;
    int mNotificationId = 100;
    int mButtonIntentRequestCodeBase = 100;
    int mPriority = Notification.PRIORITY_MAX;
    ArrayList<ILogGroup> mFilters = new ArrayList<>();
    Integer mVibrationLogLevel = null;
    Integer mBeepLogLevel = null;

    /**
     * Constructor of the setup
     *
     * @param smallIcon the small notification icon
     */
    public NotificationLoggingSetup(int smallIcon)
    {
        mSmallIcon = smallIcon;
    }

    /**
     * define a custom notification title
     * DEFAULT: Logger
     *
     * @param title the notification title
     */
    public NotificationLoggingSetup withTitle(String title)
    {
        mTitle = title;
        return this;
    }

    /**
     * define a custom big notification icon
     * DEFAULT: NONE
     *
     * @param icon the big notification icon
     */
    public NotificationLoggingSetup withBigIcon(int icon)
    {
        mBigIcon = icon;
        return this;
    }

    /**
     * define a custom notification id to avoid problems with multiple loggers or other notifications
     * DEFAULT: 100
     *
     * @param id the notification id
     */
    public NotificationLoggingSetup withNotificationId(int id)
    {
        mNotificationId = id;
        return this;
    }

    /**
     * define a custom base id for the intents that will be broadcasted via the notification button clicks
     * This number and the following 9 numbers will be used by the notification for handling the button clicks!
     * DEFAULT: 100
     *
     * @param requestCodeBase the first request code for the notifications buttons
     */
    public NotificationLoggingSetup withButtonIntentRequestCodeBase(int requestCodeBase)
    {
        mButtonIntentRequestCodeBase = requestCodeBase;
        return this;
    }

    /**
     * define a custom priority for the notification
     * DEFAULT: {@link Notification#PRIORITY_MAX}
     *
     * @param priority the notifications priority
     */
    public NotificationLoggingSetup withPriority(int priority)
    {
        mPriority = priority;
        return this;
    }

    /**
     * hand on all log groups you want this logger be able to skip through
     * DEFAULT: NONE, because the groups are defined by you
     *
     * @param filters a list of all your log groups
     */
    public NotificationLoggingSetup withFilters(ArrayList<ILogGroup> filters)
    {
        mFilters = filters;
        return this;
    }

    /**
     * enable vibration whenever a log message is written to the notification
     * DEFAULT: null (disabled)
     *
     * @param logLevel the minimum log level a log message must have to activate the vibration
     */
    public NotificationLoggingSetup withVibrateOn(Integer logLevel)
    {
        mVibrationLogLevel = logLevel;
        return this;
    }

    /**
     * enable a beep whenever a log message is written to the notification
     * DEFAULT: null (disabled)
     *
     * @param logLevel the minimum log level a log message must have to activate the beep
     */
    public NotificationLoggingSetup withBeepOn(Integer logLevel)
    {
        mBeepLogLevel = logLevel;
        return this;
    }
}