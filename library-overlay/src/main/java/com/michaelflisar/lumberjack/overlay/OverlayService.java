package com.michaelflisar.lumberjack.overlay;


import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.OverlayLoggingTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by flisar on 13.02.2017.
 */

public class OverlayService extends Service
{
    public static class OverlayServiceBinder extends Binder
    {
        private OverlayService service;

        public OverlayServiceBinder(OverlayService service)
        {
            this.service = service;
        }

        public OverlayService getService()
        {
            return service;
        }
    }

    private OverlayLoggingSetup mSetup;
    private OverlayServiceBinder mBinder = new OverlayServiceBinder(this);
    private OverlayView mView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long mLastUpdate = 0;
    private ConcurrentLinkedQueue<OverlayLoggingTree.LogEntry> mQueuedMessages = new ConcurrentLinkedQueue<>();
    private Runnable mProcessQueueRunnable = new Runnable() {
        @Override
        public void run() {
            processQueue();
        }
    };

    public void setSetup(OverlayLoggingSetup setup)
    {
        mSetup = setup;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (mView != null)
            mView.checkOrientation(this, newConfig.orientation);
    }

    public void log(final OverlayLoggingTree.LogEntry msg)
    {
        mQueuedMessages.offer(msg);
        mHandler.removeCallbacksAndMessages(null);

        // only update once every second or if last update is older than a second
        // otherwise this may be slowing down the app too much!
        if (mLastUpdate < System.currentTimeMillis() - 1000)
            mHandler.post(mProcessQueueRunnable);
        else
            mHandler.postDelayed(mProcessQueueRunnable, 1000);
    }

    private void processQueue()
    {
        if (mView == null)
        {
            mView = new OverlayView(getApplicationContext(), mSetup);
            mView.getCloseButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    destroyView();
                    stopSelf();
                }
            });
        }

        OverlayLoggingTree.LogEntry entry;
        while ((entry = mQueuedMessages.poll()) != null)
            mView.addMessage(entry);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        destroyView();
    }

    private void destroyView()
    {
        if (mView != null)
        {
            mView.hideView();
            mView = null;
        }
    }
}