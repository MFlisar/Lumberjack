package com.michaelflisar.lumberjack;

import android.app.Activity;
import android.content.Intent;

import timber.log.Timber;

/**
 * Created by flisar on 14.02.2017.
 */

public class OverlayLoggerUtil
{
    public static Boolean handleOverlayPermissionDialogResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }

    public static void initOverlayLogger(Activity activity, OverlayLoggingSetup setup)
    {
    }
}
