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

    public NotificationLoggingSetup(int smallIcon)
    {
        mSmallIcon = smallIcon;
    }

    public NotificationLoggingSetup withTitle(String title)
    {
        mTitle = title;
        return this;
    }

    public NotificationLoggingSetup withBigIcon(int icon)
    {
        mBigIcon = icon;
        return this;
    }

    public NotificationLoggingSetup withNotificationId(int id)
    {
        mNotificationId = id;
        return this;
    }

    public NotificationLoggingSetup withButtonIntentRequestCodeBase(int requestCodeBase)
    {
        mButtonIntentRequestCodeBase = requestCodeBase;
        return this;
    }

    public NotificationLoggingSetup withPriority(int priority)
    {
        mPriority = priority;
        return this;
    }

    public NotificationLoggingSetup withFilters(ArrayList<ILogGroup> filters)
    {
        mFilters = filters;
        return this;
    }

    public NotificationLoggingSetup withVibrateOn(int logLevel)
    {
        mVibrationLogLevel = logLevel;
        return this;
    }

    public NotificationLoggingSetup withBeepOn(int logLevel)
    {
        mBeepLogLevel = logLevel;
        return this;
    }
}