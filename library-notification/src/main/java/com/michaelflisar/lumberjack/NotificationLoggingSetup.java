package com.michaelflisar.lumberjack;

import android.app.Notification;
import android.content.Context;

/**
 * Created by Michael on 17.10.2016.
 */

public class NotificationLoggingSetup
{
    String mTitle = "Logger";
    int mBigIcon = -1;
    int mSmallIcon = -1;
    int mNotificationId = 100;
    int mButtonIntentRequestCodeNext = 100;
    int mButtonIntentRequestCodePrev = 101;
    int mButtonIntentRequestCodeFirst = 102;
    int mButtonIntentRequestCodeLast = 103;
    int mButtonIntentRequestCodeCancel = 104;
    int mButtonIntentRequestCodeSettings = 105;
    int mPriority = Notification.PRIORITY_MAX;

    public NotificationLoggingSetup()
    {

    }

    public NotificationLoggingSetup withTitle(String title)
    {
        mTitle = title;
        return this;
    }

    public NotificationLoggingSetup withSmallIcon(int icon)
    {
        mSmallIcon = icon;
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

    public NotificationLoggingSetup withButtonIntentRequestCode(int requestCodePrev, int requestCodeNext, int requestCodeFirst, int requestCodeLast, int requestCodeCancel, int requestCodeSettings)
    {
        mButtonIntentRequestCodePrev = requestCodePrev;
        mButtonIntentRequestCodeNext = requestCodeNext;
        mButtonIntentRequestCodeFirst = requestCodeFirst;
        mButtonIntentRequestCodeLast = requestCodeLast;
        mButtonIntentRequestCodeCancel = requestCodeCancel;
        mButtonIntentRequestCodeSettings = requestCodeSettings;
        return this;
    }

    public NotificationLoggingSetup withPriority(int priority)
    {
        mPriority = priority;
        return this;
    }
}