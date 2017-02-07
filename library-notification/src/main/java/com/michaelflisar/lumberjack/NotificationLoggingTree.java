package com.michaelflisar.lumberjack;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.michaelflisar.lumberjack.notification.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import timber.log.BaseTree;

/**
 * Created by Michael on 17.10.2016.
 */

public class NotificationLoggingTree extends BaseTree
{
    public static final String BROADCAST_ACTION = "com.michaelflisar.lumberjack.NotificationLoggingTree.BROADCAST_ACTION";

    private static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";

    private static HashSet<WeakReference<NotificationLoggingTree>> mTrees = new HashSet<>();

    private Context mContext;
    private long mLastUpdate;
    private Handler mHandler;
    private Runnable mUpdateRunnable;
    private NotificationLoggingSetup mSetup;
    private ArrayList<LogEntry> mLogs;
    private int mErrors;
    private ArrayList<LogEntry> mFilteredLogs;
    private Integer mCurrentFilter;
    private int mLogIndex = -1;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews mRemoteView;
    private NotificationCompat.Builder mBuilder;
    private Vibrator mVibrator = null;
    private ToneGenerator mToneGenerator = null;

    public NotificationLoggingTree(Context context, boolean combineTags, NotificationLoggingSetup setup)
    {
        super(combineTags, false);

        if (setup == null || context == null)
            throw new RuntimeException("You can't create a NotificationLoggingTree without providing a setup and a context!");

        if (context instanceof Activity || context instanceof Service)
            Log.w(NotificationLoggingTree.class.getSimpleName(), "You should provide an Application context to avoid leaks!");

        mContext = context;
        mHandler = new Handler();
        mLastUpdate = 0L;
        mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
            }
        };
        mLogs = new ArrayList<>();
        mErrors = 0;
        mFilteredLogs = new ArrayList<>();
        mCurrentFilter = null;
        mSetup = setup;
        init();

        mTrees.add(new WeakReference<>(this));
    }

    private void init()
    {
        // 1) Prepare intents
        PendingIntent pIntentNext = getPI(mSetup.mButtonIntentRequestCodeBase);
        PendingIntent pIntentPrev = getPI(mSetup.mButtonIntentRequestCodeBase + 1);
        PendingIntent pIntentFirst = getPI(mSetup.mButtonIntentRequestCodeBase + 2);
        PendingIntent pIntentLast = getPI(mSetup.mButtonIntentRequestCodeBase + 3);
        PendingIntent pIntentCancel = getPI(mSetup.mButtonIntentRequestCodeBase + 4);
        PendingIntent pIntentSettings = getPI(mSetup.mButtonIntentRequestCodeBase + 5);
        PendingIntent pIntentFilterPrev = getPI(mSetup.mButtonIntentRequestCodeBase + 6);
        PendingIntent pIntentFilterNext = getPI(mSetup.mButtonIntentRequestCodeBase + 7);
        PendingIntent pIntentFilterClear = getPI(mSetup.mButtonIntentRequestCodeBase + 8);

        // 2) Create custom view
        mRemoteView = new RemoteViews(mContext.getPackageName(), R.layout.lumberjack_notification);
        if (mSetup.mBigIcon > 0)
            mRemoteView.setImageViewResource(R.id.ivIcon, mSetup.mBigIcon);
        else
            mRemoteView.setViewVisibility(R.id.ivIcon, View.GONE);
        mRemoteView.setTextViewText(R.id.tvText, "");
        mRemoteView.setTextViewText(R.id.tvTitle, mSetup.mTitle);
        mRemoteView.setTextViewText(R.id.tvFilter, "");
        mRemoteView.setOnClickPendingIntent(R.id.btPrev, pIntentPrev);
        mRemoteView.setOnClickPendingIntent(R.id.btNext, pIntentNext);
        mRemoteView.setOnClickPendingIntent(R.id.btFirst, pIntentFirst);
        mRemoteView.setOnClickPendingIntent(R.id.btLast, pIntentLast);
//        mRemoteView.setOnClickPendingIntent(R.id.btCancel, pIntentCancel);
        mRemoteView.setOnClickPendingIntent(R.id.btSettings, pIntentSettings);
        if (mSetup.mFilters == null || mSetup.mFilters.size() == 0)
            mRemoteView.setViewVisibility(R.id.llFilter, View.GONE);
        else
        {
            mRemoteView.setOnClickPendingIntent(R.id.btFilterNext, pIntentFilterNext);
            mRemoteView.setOnClickPendingIntent(R.id.btFilterPrev, pIntentFilterPrev);
            mRemoteView.setOnClickPendingIntent(R.id.btFilterClear, pIntentFilterClear);
        }

        // TODO: settings
        mRemoteView.setViewVisibility(R.id.btSettings, View.GONE);

        // we make the notification removeable via swipe!
        mRemoteView.setViewVisibility(R.id.btCancel, View.GONE);

        // 3) Create notification
        mBuilder = new NotificationCompat.Builder(mContext)
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

        mNotificationManager = NotificationManagerCompat.from(mContext);
        mNotificationManager.notify(mSetup.mNotificationId, mBuilder.build());

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BROADCAST_ACTION);
//        context.registerReceiver(new NotificationBroadcastReceiver(), intentFilter);
    }

    private PendingIntent getPI(int requestCode)
    {
        Intent i = new Intent()
                .setAction(BROADCAST_ACTION)
                .putExtra(EXTRA_REQUEST_CODE,requestCode);
        PendingIntent pi = PendingIntent.getBroadcast(mContext,requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t)
    {
        String logMessage = L.getFormatter().formatLine(tag, message);
        mLogs.add(new LogEntry(priority, L.getFormatter().extractGroupFromTag(tag), logMessage));
        if (priority >= Log.ERROR)
            mErrors++;
        mLogIndex = mLogs.size() - 1;
        updateFilterData(true);
        postUpdateNotification();

        if (mSetup.mVibrationLogLevel != null && mSetup.mVibrationLogLevel <= priority)
        {
            if (mVibrator == null)
                mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(150);
        }
        if (mSetup.mBeepLogLevel != null && mSetup.mBeepLogLevel <= priority)
        {
            if (mToneGenerator == null)
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            mToneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        }
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

    private void updateFilterData(boolean checkLastOnly)
    {
        int i = 0;
        if (checkLastOnly)
            i = mLogs.size() - 1;
        else
            mFilteredLogs.clear();

        for (; i < mLogs.size(); i++)
        {
            if (mCurrentFilter == null || (mLogs.get(i).group != null && mSetup.mFilters.get(mCurrentFilter).getTag().equals(mLogs.get(i).group)))
                mFilteredLogs.add(mLogs.get(i));
        }

        mLogIndex = mFilteredLogs.size() - 1;
    }

    private void updateNotification()
    {
        LogEntry entry = mLogIndex == -1 ? null : mFilteredLogs.get(mLogIndex);
        String textExpanded = entry == null ? mContext.getString(R.string.lumberjack_notification_no_log_for_filter_found) : entry.message;
        int textColor = entry == null ? Color.BLACK : entry.getColor();
        String titleCollapsed = mContext.getString(R.string.lumberjack_notification_title, mFilteredLogs.size());
        String titleExpanded = mContext.getString(R.string.lumberjack_notification_title_expanded, mLogIndex + 1, mFilteredLogs.size(), getErrors());
        String textFilter = null;
        if (mCurrentFilter == null)
            textFilter = mContext.getString(R.string.lumberjack_notification_filter_disabled);
        else
            textFilter = mContext.getString(R.string.lumberjack_notification_filter, mSetup.mFilters.get(mCurrentFilter).getTag(), mCurrentFilter + 1, mSetup.mFilters.size());
        mBuilder.setContentText(titleCollapsed);
        mRemoteView.setTextViewText(R.id.tvTitle, titleExpanded);
        mRemoteView.setTextViewText(R.id.tvText, textExpanded);
        mRemoteView.setTextColor(R.id.tvText, textColor);
        mRemoteView.setTextViewText(R.id.tvFilter, textFilter);
//        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(log).setBigContentTitle("Log: " + (mLogIndex + 1) + "/" + mLogs.size()));
//        mBuilder.setContentText("Log: " + (mLogIndex + 1) + "/" + mLogs.size());
        mNotificationManager.notify(mSetup.mNotificationId, mBuilder.build());

        mLastUpdate = System.currentTimeMillis();
    }

    private void removeNotification()
    {
        mNotificationManager.cancel(mSetup.mNotificationId);
    }

    private int getErrors()
    {
        if (mCurrentFilter == null)
            return mErrors;

        int errors = 0;
        for (int i = 0; i < mFilteredLogs.size(); i++)
        {
            if (mFilteredLogs.get(i).priority >= Log.ERROR)
                errors++;
        }
        return errors;
    }

    private static final class LogEntry
    {
        private int priority;
        private String group;
        private String message;

        public LogEntry(int priority, String group, String message)
        {
            this.priority = priority;
            this.group = group;
            this.message = message;
        }

        protected int getColor()
        {
            int color = Color.BLACK;
            switch (priority)
            {
                case Log.VERBOSE:
                case Log.DEBUG:
                    break;
                case Log.INFO:
                case Log.WARN:
                    color = Color.GRAY;
                    break;
                case Log.ERROR:
                case Log.ASSERT:
                    color = Color.RED;
            }
            return color;
        }
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

                        // 1) Next
                        if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase)
                        {
                            if (tree.get().mLogIndex != -1)
                            {
                                if (tree.get().mLogIndex < tree.get().mFilteredLogs.size() - 1)
                                {
                                    tree.get().mLogIndex++;
                                    tree.get().updateNotification();
                                }
                                else if (tree.get().mLogIndex == tree.get().mFilteredLogs.size() - 1)
                                {
                                    tree.get().mLogIndex = 0;
                                    tree.get().updateNotification();
                                }
                            }
                        }
                        // 2) Prev
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 1)
                        {
                            if (tree.get().mLogIndex != -1)
                            {
                                if (tree.get().mLogIndex > 0)
                                {
                                    tree.get().mLogIndex--;
                                    tree.get().updateNotification();
                                }
                                else if (tree.get().mLogIndex == 0)
                                {
                                    tree.get().mLogIndex = tree.get().mFilteredLogs.size() - 1;
                                    tree.get().updateNotification();
                                }
                            }
                        }
                        // 3) First
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 2)
                        {
                            if (tree.get().mLogIndex != -1 && tree.get().mLogIndex != 0)
                            {
                                tree.get().mLogIndex = 0;
                                tree.get().updateNotification();
                            }
                        }
                        // 4) Last
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 3)
                        {
                            if (tree.get().mLogIndex != -1 && tree.get().mLogIndex != tree.get().mFilteredLogs.size() - 1)
                            {
                                tree.get().mLogIndex = tree.get().mFilteredLogs.size() - 1;
                                tree.get().updateNotification();
                            }
                        }
                        // 5) Cancel
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 4)
                        {
                            tree.get().removeNotification();
                        }
                        // 6) Settings
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 5)
                        {

                        }
                        // 7) Filter Prev
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 6)
                        {
                            if (tree.get().mCurrentFilter == null)
                                tree.get().mCurrentFilter = 0;
                            else if (tree.get().mCurrentFilter > 0)
                                tree.get().mCurrentFilter--;
                            else if (tree.get().mCurrentFilter == 0)
                                tree.get().mCurrentFilter = tree.get().mSetup.mFilters.size() - 1;
                            else
                                return;

                            tree.get().updateFilterData(false);
                            tree.get().updateNotification();
                        }
                        // 8) Filter Next
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 7)
                        {
                            if (tree.get().mCurrentFilter == null)
                                tree.get().mCurrentFilter = 0;
                            else if (tree.get().mCurrentFilter < tree.get().mSetup.mFilters.size() - 1)
                                tree.get().mCurrentFilter++;
                            else if (tree.get().mCurrentFilter == tree.get().mSetup.mFilters.size() - 1)
                                tree.get().mCurrentFilter = 0;
                            else
                                return;

                            tree.get().updateFilterData(false);
                            tree.get().updateNotification();
                        }
                        // 9) Filter Clear
                        else if (requestCode == tree.get().mSetup.mButtonIntentRequestCodeBase + 8)
                        {
                            if (tree.get().mCurrentFilter != null)
                            {
                                tree.get().mCurrentFilter = null;
                                tree.get().updateFilterData(false);
                                tree.get().updateNotification();
                            }
                        }
                    }
                }
            }
        }
    }
}