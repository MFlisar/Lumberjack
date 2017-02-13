package com.michaelflisar.lumberjack.overlay;


import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.OverlayLoggingTree;

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

    private OverlayServiceBinder binder = new OverlayServiceBinder(this);
    private OverlayView view;

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (view != null)
            view.checkOrientation(this, newConfig.orientation);
    }

    public void log(OverlayLoggingTree.LogEntry msg, OverlayLoggingSetup setup)
    {
        if (view == null)
        {
            view = new OverlayView(getApplicationContext(), setup);
            view.getCloseButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    destroyView();
                    stopSelf();
                }
            });
        }
        view.addMessage(msg);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        destroyView();
    }

    private void destroyView()
    {
        if (view != null)
        {
            view.hideView();
            view = null;
        }
    }
}