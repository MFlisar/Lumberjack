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
    private boolean mPaused = false;

    private boolean mErrorFilterActive;

    public void setSetup(OverlayLoggingSetup setup)
    {
        mSetup = setup;
        mErrorFilterActive = mSetup.getStartWithShowErrorsOnly();
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
            mView.checkOrientation(newConfig.orientation);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mPaused = false;
        showView();
        processQueue();
        return super.onStartCommand(intent, flags, startId);
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
        if (mPaused)
            return;

        if (mView == null)
            createView();

        OverlayLoggingTree.LogEntry entry;
        while ((entry = mQueuedMessages.poll()) != null)
            mView.addMessage(entry);
    }

    @Override
    public void onDestroy()
    {
        destroyView();
        super.onDestroy();
    }

    private void createView()
    {
        if (mView != null)
            return;

        mView = new OverlayView(getApplicationContext(), mSetup, mErrorFilterActive);
        mView.getCloseButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                destroyView();
                stopSelf();
            }
        });
        mView.getErrorButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mErrorFilterActive = !mErrorFilterActive;
                mView.updateErrorFilter(mErrorFilterActive);
            }
        });
        mView.getPauseButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OverlayNotification.show(OverlayService.this, mSetup);
                hideView();
                mPaused = true;
            }
        });
    }

    private void hideView()
    {
        if (mView != null)
            mView.hideView();
    }

    private void showView()
    {
        if (mView != null)
            mView.showView();
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