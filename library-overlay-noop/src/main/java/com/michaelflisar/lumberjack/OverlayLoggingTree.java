package com.michaelflisar.lumberjack;

import android.content.Context;
import android.content.Intent;

import com.michaelflisar.lumberjack.filter.ILogFilter;

import timber.log.BaseTree;

/**
 * Created by flisar on 13.02.2017.
 */

public class OverlayLoggingTree extends BaseTree
{
    public OverlayLoggingTree(Context context, boolean combineTags, OverlayLoggingSetup setup, ILogFilter filter)
    {
        super(combineTags, false, filter);
    }

    public boolean checkRequestPermissionResult(int requestCode, int resultCode, Intent data)
    {
        return false;
    }

    @Override
    protected boolean isReady()
    {
        return false;
    }

    @Override
    protected void doLog(int priority, String tag, String message, Throwable t)
    {
    }
}
