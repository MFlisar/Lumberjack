package com.michaelflisar.lumberjack;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.michaelflisar.lumberjack.notification.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import timber.log.DebugTree;

/**
 * Created by Michael on 17.10.2016.
 */

public class NotificationLoggingTree extends DebugTree
{
    public static final String BROADCAST_ACTION = "com.michaelflisar.lumberjack.NotificationLoggingTree.BROADCAST_ACTION";

    private static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";

    private static HashSet<WeakReference<NotificationLoggingTree>> mTrees = new HashSet<>();

    private long mLastUpdate;
    private Handler mHandler;
    private Runnable mUpdateRunnable;
    private NotificationLoggingSetup mSetup;
    private ArrayList<String> mLogs;
    private int mLogIndex = -1;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews mRemoteView;
    private NotificationCompat.Builder mBuilder;

    public NotificationLoggingTree(Context context, boolean combineTags, NotificationLoggingSetup setup)
    {
        super(combineTags);

        if (setup == null || context == null)
            throw new RuntimeException("You can't create a NotificationLoggingTree without providing a setup and a context!");

        mHandler = new Handler();
        mLastUpdate = 0L;
        mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
            }
        };
        mLogs = new ArrayList<>();
        mSetup = setup;
        init(context);

        mTrees.add(new WeakReference<>(this));
    }

    private void init(Context context)
    {
        // 1) Prepare intents
        Intent iNext = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodeNext);
        PendingIntent pIntentNext = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodeNext, iNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent iPrev = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodePrev);
        PendingIntent pIntentPrev = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodePrev, iPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent iFirst = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodeFirst);
        PendingIntent pIntentFirst = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodeFirst, iFirst, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent iLast = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodeLast);
        PendingIntent pIntentLast = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodeLast, iLast, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent iCancel = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodeCancel);
        PendingIntent pIntentCancel = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodeCancel, iCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent iSettings = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE, mSetup.mButtonIntentRequestCodeSettings);
        PendingIntent pIntentSettings = PendingIntent.getBroadcast(context, mSetup.mButtonIntentRequestCodeSettings, iSettings, PendingIntent.FLAG_UPDATE_CURRENT);

        // 2) Create custom view
        mRemoteView = new RemoteViews(context.getPackageName(), R.layout.lumberjack_notification);
        if (mSetup.mBigIcon > 0)
            mRemoteView.setImageViewResource(R.id.ivIcon, mSetup.mBigIcon);
        else
            mRemoteView.setViewVisibility(R.id.ivIcon, View.GONE);
        mRemoteView.setTextViewText(R.id.tvText, "");
        mRemoteView.setTextViewText(R.id.tvTitle, mSetup.mTitle);
        mRemoteView.setOnClickPendingIntent(R.id.btPrev, pIntentPrev);
        mRemoteView.setOnClickPendingIntent(R.id.btNext, pIntentNext);
        mRemoteView.setOnClickPendingIntent(R.id.btFirst, pIntentFirst);
        mRemoteView.setOnClickPendingIntent(R.id.btLast, pIntentLast);
//        mRemoteView.setOnClickPendingIntent(R.id.btCancel, pIntentCancel);
        mRemoteView.setOnClickPendingIntent(R.id.btSettings, pIntentSettings);

        // TODO: settings
        mRemoteView.setViewVisibility(R.id.btSettings, View.GONE);

        // we make the notification removeable via swipe!
        mRemoteView.setViewVisibility(R.id.btCancel, View.GONE);

        // 3) Create notification
        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(mSetup.mTitle)
//                .setAutoCancel(false)
//                .setOngoing(true)
                .setPriority(mSetup.mPriority)
                .setOnlyAlertOnce(true)
                .setCustomBigContentView(mRemoteView)
//                .setContentIntent(
//                        PendingIntent.getActivity(context, 10,
//                                new Intent(context, FrontActivity.class)
//                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
//                                0)
//                )
//                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, mSetup.mPrevLabel, pIntentPrev))
//                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, mSetup.mNextLabel, pIntentNext))
//                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_ff, mSetup.mLastLabel, pIntentLast))
        ;

//
//        notification.contentView = contentView;

        if (mSetup.mSmallIcon > 0)
            mBuilder.setSmallIcon(mSetup.mSmallIcon);

        mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(mSetup.mNotificationId, mBuilder.build());

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BROADCAST_ACTION);
//        context.registerReceiver(new NotificationBroadcastReceiver(), intentFilter);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t)
    {
        String logMessage = L.getFormatter().formatLine(tag, message);
        mLogs.add(logMessage);
        mLogIndex = mLogs.size() - 1;
        postUpdateNotification();
    }

    private void postUpdateNotification()
    {
        mHandler.removeCallbacksAndMessages(null);

        // only update once every second or if last update is older than a second
        // otherwise this may be slowing down the app too much!
        if (mLastUpdate < System.currentTimeMillis() - 1000)
            updateNotification();
        else
            mHandler.postDelayed(mUpdateRunnable, 1000);
    }

    private void updateNotification()
    {
        String log = mLogs.get(mLogIndex);
        mRemoteView.setTextViewText(R.id.tvTitle, "Log: " + (mLogIndex + 1) + "/" + mLogs.size());
        mRemoteView.setTextViewText(R.id.tvText, log);
//        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(log).setBigContentTitle("Log: " + (mLogIndex + 1) + "/" + mLogs.size()));
//        mBuilder.setContentText("Log: " + (mLogIndex + 1) + "/" + mLogs.size());
        mNotificationManager.notify(mSetup.mNotificationId, mBuilder.build());

        mLastUpdate = System.currentTimeMillis();
    }

    private void removeNotification()
    {
        mNotificationManager.cancel(mSetup.mNotificationId);
    }

    public static class NotificationBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null || intent.getAction() == null)
                return;

            if (intent.getAction().equals(BROADCAST_ACTION))
            {
                Iterator<WeakReference<NotificationLoggingTree>> it = mTrees.iterator();
                while (it.hasNext())
                {
                    WeakReference<NotificationLoggingTree> tree = it.next();
                    if (tree == null || tree.get() == null)
                    {
                        // clean up HashSet
                        it.remove();
                    }
                    else if (intent.getExtras() != null)
                    {
                        int requestCode = intent.getExtras().getInt(EXTRA_REQUEST_CODE, -1);
                        if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeNext)
                        {
                            if (tree.get().mLogIndex < tree.get().mLogs.size() - 1)
                            {
                                tree.get().mLogIndex++;
                                tree.get().updateNotification();
                            }
                        }
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodePrev)
                        {
                            if (tree.get().mLogIndex > 0)
                            {
                                tree.get().mLogIndex--;
                                tree.get().updateNotification();
                            }
                        }
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeFirst)
                        {
                            if (tree.get().mLogIndex != 0)
                            {
                                tree.get().mLogIndex = 0;
                                tree.get().updateNotification();
                            }
                        }
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeLast)
                        {
                            if (tree.get().mLogIndex != tree.get().mLogs.size() - 1)
                            {
                                tree.get().mLogIndex = tree.get().mLogs.size() - 1;
                                tree.get().updateNotification();
                            }
                        }
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeSettings)
                        {
                           // TODO
                        }
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeCancel)
                        {
                            tree.get().removeNotification();
                        }
                    }
                }
            }
        }
    }
}