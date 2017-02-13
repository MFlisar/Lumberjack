package com.michaelflisar.lumberjack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.michaelflisar.lumberjack.overlay.*;
import com.michaelflisar.lumberjack.overlay.BuildConfig;

import java.lang.annotation.Target;

import timber.log.BaseTree;

/**
 * Created by flisar on 13.02.2017.
 */

public class OverlayLoggingTree extends BaseTree
{
    private OverlayLoggingSetup mSetup;
    private Context mContext;
    private boolean mPermissionsGranted;

    public OverlayLoggingTree(Context context, boolean combineTags, OverlayLoggingSetup setup)
    {
        super(combineTags, false);

        if (setup == null || context == null)
            throw new RuntimeException("You can't create a OverlayLoggingTree without providing a setup and a context!");

        // we must ask for permission!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context))
        {
            mPermissionsGranted = false;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            if (!(context instanceof Activity))
                throw new RuntimeException("On android API 23 and above, we must manually accept the DRAW OVERLAY permission, therefore you must provide an activity context and handle the activity result in the activity!");
            ((Activity)context).startActivityForResult(intent, setup.getPermissionRequestCode());
        }
        else
            mPermissionsGranted = true;

        mContext = context.getApplicationContext();
        mSetup = setup;
    }

    public boolean checkRequestPermissionResult(int requestCode, int resultCode, Intent data)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == mSetup.getPermissionRequestCode())
            mPermissionsGranted = Settings.canDrawOverlays(mContext);
        return mPermissionsGranted;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t)
    {
        // we can't log anything before we've got the permission!
        if (!mPermissionsGranted)
            return;

        String logMessage = L.getFormatter().formatLine(tag, message);
        DebugOverlay.with(mContext, mSetup).log(new LogEntry(priority, L.getFormatter().extractGroupFromTag(tag), logMessage, mSetup.getColor(priority), mSetup.getTextSize()));
    }

    public static final class LogEntry
    {
        private int priority;
        private String group;
        private String message;
        private int color;
        private int textSizeInDp;

        public LogEntry(int priority, String group, String message, int color, int textSizeInDp)
        {
            this.priority = priority;
            this.group = group;
            this.message = message;
            this.color = color;
            this.textSizeInDp = textSizeInDp;
        }

        public String getMessage()
        {
            return message;
        }

        public int getColor()
        {
            return color;
        }

        public int getTextSizeInDp()
        {
            return textSizeInDp;
        }
    }
}
