package com.michaelflisar.lumberjack.overlay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.OverlayLoggingTree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by flisar on 13.02.2017.
 */
public class DebugOverlay
{
    private static DebugOverlay INSTANCE;

    private MessageDispatcher messageDispatcher;

    private DebugOverlay(Context context, final OverlayLoggingSetup setup)
    {
        Intent intent = new Intent(context, OverlayService.class);
        ServiceConnection serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder)
            {
                OverlayService service = ((OverlayService.OverlayServiceBinder) binder).getService();
                messageDispatcher.setSetup(setup);
                messageDispatcher.setService(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                INSTANCE = null;
            }
        };

        messageDispatcher = new MessageDispatcher();
        boolean bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (!bound)
            throw new RuntimeException("Could not bind the Service " + OverlayService.class.getSimpleName() + " -  Is Service declared in Android manifest and is Permission SYSTEM_ALERT_WINDOW granted?");
    }

    public static DebugOverlay with(Context context, OverlayLoggingSetup setup)
    {
        if (INSTANCE == null)
            INSTANCE = new DebugOverlay(context.getApplicationContext(), setup);
        return INSTANCE;
    }

    public void log(OverlayLoggingTree.LogEntry msg)
    {
        messageDispatcher.enqueueMessage(msg);
    }

    private static class MessageDispatcher
    {
        private OverlayService service;
        private Queue<OverlayLoggingTree.LogEntry> messageQueue = new LinkedList<>();
        private OverlayLoggingSetup mSetup;

        void setService(@NonNull OverlayService service)
        {
            if (service == null)
                throw new NullPointerException(OverlayService.class.getSimpleName() + " is null! That's not allowed");
            this.service = service;
            OverlayLoggingTree.LogEntry entry;
            while ((entry = messageQueue.poll()) != null)
                dispatch(entry);
        }

        void setSetup(OverlayLoggingSetup setup)
        {
            mSetup = setup;
        }

        public void enqueueMessage(@NonNull OverlayLoggingTree.LogEntry msg)
        {
            if (service != null)
                dispatch(msg);
            else
                messageQueue.add(msg);
        }

        private void dispatch(OverlayLoggingTree.LogEntry message)
        {
            if (service == null)
                throw new NullPointerException(OverlayService.class.getSimpleName() + " is null, but this should never be the case");
            service.log(message, mSetup);
        }
    }
}