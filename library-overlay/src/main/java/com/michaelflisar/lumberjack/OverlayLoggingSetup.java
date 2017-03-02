package com.michaelflisar.lumberjack;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by flisar on 13.02.2017.
 */

public class OverlayLoggingSetup
{
    private int mBackgroundColor = Color.parseColor("#64000000");
    private int mColorVerbose = Color.WHITE;
    private int mColorDebug = Color.WHITE;
    private int mColorInfo = Color.WHITE;
    private int mColorWarn = Color.WHITE;
    private int mColorError = Color.RED;
    private int mColorAssert = Color.RED;
    private int mTextSizeInDp = 12;
    private int mOverlayRecyclerViewHeightInDp = 100;
    private boolean mStartExpanded = true;
    private int mDefaultMinVisibleLogPriority = Log.VERBOSE;

    private int mPermissionRequestCode = 200;
    private String mNotificationTitle = "Overlay logger";
    private String mNotificationText = "Currently paused, click to resume";
    private int mNotificationId = 500;

    public OverlayLoggingSetup()
    {

    }

    /**
     * define a custom background for the overlay
     * DEFAULT: #64000000 (light transparent grey)
     *
     * @param backgroundColor The background color
     */
    public OverlayLoggingSetup withBackgroundColor(int backgroundColor)
    {
        mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * define a custom message color for verbose logs
     * DEFAULT: white
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorVerbose(int color)
    {
        mColorVerbose = color;
        return this;
    }

    /**
     * define a custom message color for debug logs
     * DEFAULT: white
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorDebug(int color)
    {
        mColorDebug = color;
        return this;
    }

    /**
     * define a custom message color for info logs
     * DEFAULT: white
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorInfo(int color)
    {
        mColorInfo = color;
        return this;
    }

    /**
     * define a custom message color for warn logs
     * DEFAULT: white
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorWarn(int color)
    {
        mColorWarn = color;
        return this;
    }

    /**
     * define a custom message color for error logs
     * DEFAULT: red
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorError(int color)
    {
        mColorError = color;
        return this;
    }

    /**
     * define a custom message color for assert logs
     * DEFAULT: red
     *
     * @param color The color
     */
    public OverlayLoggingSetup withColorAssert(int color)
    {
        mColorAssert = color;
        return this;
    }

    /**
     * define a custom request code for the permission request dialog
     * DEFAULT: 200
     *
     * @param requestCode The request code
     */
    public OverlayLoggingSetup withPermissionRequestCode(int requestCode)
    {
        mPermissionRequestCode = requestCode;
        return this;
    }

    /**
     * define a custom text size for log messages
     * DEFAULT: 12dp
     *
     * @param dp The text size in dp
     */
    public OverlayLoggingSetup withTextSizeInDp(int dp)
    {
        mTextSizeInDp = dp;
        return this;
    }

    /**
     * define a custom height for the overlays recycler view
     * DEFAULT: 100dp
     *
     * @param dp The overlay's recycler view height in dp
     */
    public OverlayLoggingSetup withOverlayRecyclerViewHeightInDp(int dp)
    {
        mOverlayRecyclerViewHeightInDp = dp;
        return this;
    }

    /**
     * start the overlay logger in an expanded state
     * DEFAULT: true
     *
     * @param startExpanded true to start the overlay logger expanded
     */
    public OverlayLoggingSetup withStartExpanded(boolean startExpanded)
    {
        mStartExpanded = startExpanded;
        return this;
    }

    /**
     * default value for miimimum visible log priority, by default all logs are visible
     * DEFAULT: Log.VERBOSE
     *
     * @param logPriority define the minimum log level that is visible in the overlay logger
     */
    public OverlayLoggingSetup withDefaultMinimumVisibleLogPriority(int logPriority)
    {
        mDefaultMinVisibleLogPriority = logPriority;
        return this;
    }

    /**
     * the title of the notificaton that is shown when overlay logger is paused
     *
     * @param notificationTitle title of the notification
     */
    public OverlayLoggingSetup withNotificationTitle(String notificationTitle)
    {
        mNotificationTitle = notificationTitle;
        return this;
    }

    /**
     * the text of the notificaton that is shown when overlay logger is paused
     *
     * @param notificationText text of the notification
     */
    public OverlayLoggingSetup withNotificationText(String notificationText)
    {
        mNotificationText = notificationText;
        return this;
    }

    /**
     * the id of the notificaton that is shown when overlay logger is paused
     * DEFAULT: 500
     *
     * @param notificationId true to start the overlay logger with active error filter
     */
    public OverlayLoggingSetup withStartWithShowErrorsOnly(int notificationId)
    {
        mNotificationId = notificationId;
        return this;
    }

    public int getBackgroundColor()
    {
        return mBackgroundColor;
    }

    public int getPermissionRequestCode()
    {
        return mPermissionRequestCode;
    }

    public int getTextSize()
    {
        return mTextSizeInDp;
    }

    public int getOverlayRecyclerViewHeight()
    {
        return mOverlayRecyclerViewHeightInDp;
    }

    public boolean getWithStartExpanded()
    {
        return mStartExpanded;
    }

    public int getDefaultMinVisibleLogPriority()
    {
        return mDefaultMinVisibleLogPriority;
    }

    public String getNotificationTitle()
    {
        return mNotificationTitle;
    }

    public String getNotificationText()
    {
        return mNotificationText;
    }

    public int getNotificationId()
    {
        return mNotificationId;
    }

    public int getColor(int priority)
    {
        switch (priority)
        {
            case Log.VERBOSE:
                return mColorVerbose;
            case Log.DEBUG:
                return mColorDebug;
            case Log.INFO:
                return mColorInfo;
            case Log.WARN:
                return mColorWarn;
            case Log.ERROR:
                return mColorError;
            case Log.ASSERT:
                return mColorAssert;
        }
        return mColorDebug;
    }
}
