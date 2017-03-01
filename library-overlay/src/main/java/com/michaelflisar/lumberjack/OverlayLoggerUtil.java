package com.michaelflisar.lumberjack;

import android.app.Activity;
import android.content.Intent;

import com.michaelflisar.lumberjack.filter.ILogFilter;

import timber.log.Timber;

/**
 * Created by flisar on 14.02.2017.
 */

public class OverlayLoggerUtil
{
    /*
     * This function makes sure, that the overlay logging tree is updated and knows, that it
     * got the overlay permissions and can start drawing over apps and show it's logs
     *
     * This function only works for a single overlay tree and looks for this one in timber and
     * forwards the result if it finds the tree
     *
     * @return true/false if the overlay permission is granted, null if no {@link OverlayLoggingTree} was planted yet
     * (you must call {@initOverlayLogger} and let the function start the permission dialog)
     */
    public static Boolean handleOverlayPermissionDialogResult(int requestCode, int resultCode, Intent data)
    {
        Integer index = null;
        for (int i = 0; i < Timber.forest().size(); i++)
        {
            if (Timber.forest().get(i) instanceof OverlayLoggingTree)
            {
                index = i;
                break;
            }
        }

        if (index == null)
            return null;

        return ((OverlayLoggingTree) Timber.forest().get(index)).checkRequestPermissionResult(requestCode, resultCode, data);
    }

    public static void initOverlayLogger(Activity activity, OverlayLoggingSetup setup, ILogFilter filter)
    {
        for (int i = 0; i < Timber.forest().size(); i++)
        {
            if (Timber.forest().get(i) instanceof OverlayLoggingTree)
                return;
        }
        Timber.plant(new OverlayLoggingTree(activity, true, setup, filter));
    }
}
