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
    private int mOverlayHeightInDp = 150;

    private int mPermissionRequestCode = 200;

    public OverlayLoggingSetup()
    {

    }

    public OverlayLoggingSetup withBackgroundColor(int backgroundColor)
    {
        mBackgroundColor = backgroundColor;
        return this;
    }

    public OverlayLoggingSetup withColorVerbose(int colorVerbose)
    {
        mColorVerbose = colorVerbose;
        return this;
    }

    public OverlayLoggingSetup withColorDebug(int colorDebug)
    {
        mColorDebug = colorDebug;
        return this;
    }

    public OverlayLoggingSetup withColorInfo(int colorInfo)
    {
        mColorInfo = colorInfo;
        return this;
    }

    public OverlayLoggingSetup withColorWarn(int colorWarn)
    {
        mColorWarn = colorWarn;
        return this;
    }

    public OverlayLoggingSetup withColorError(int colorError)
    {
        mColorError = colorError;
        return this;
    }

    public OverlayLoggingSetup withColorAssert(int colorAssert)
    {
        mColorAssert = colorAssert;
        return this;
    }

    public OverlayLoggingSetup withPermissionRequestCode(int requestCode)
    {
        mPermissionRequestCode = requestCode;
        return this;
    }

    public OverlayLoggingSetup withTextSizeInDp(int dp)
    {
        mTextSizeInDp = dp;
        return this;
    }

    public OverlayLoggingSetup withOverlayHeightInDp(int dp)
    {
        mOverlayHeightInDp = dp;
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

    public int getOverlayHeight()
    {
        return mOverlayHeightInDp;
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
