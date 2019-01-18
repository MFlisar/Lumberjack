package com.michaelflisar.lumberjack.overlay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.OverlayLoggingTree;

import java.lang.reflect.Field;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Created by flisar on 13.02.2017.
 */

class OverlayView extends FrameLayout
{
    private OverlayLoggingSetup mSetup;
    private LinearLayout mLLHeader;
    private ImageView mCloseButton;
    private ImageView mCollapseExpandButton;
    private ImageView mPauseButton;
    private TextView mFilterButton;
    private TextView mVerboseButton;
    private TextView mDebugButton;
    private TextView mInfoButton;
    private TextView mWarnButton;
    private TextView mErrorButton;
    private LinearLayout mLLFilters;
    private TextView mLabel;
    private TextView mLabelErrors;
    private RecyclerView mRecyclerView;
    private WindowManager mWindowManager;
    private int mErrors = 0;
    private LogAdapter mAdapter;
    private boolean mExpanded = true;
    private boolean mFilterShown = false;
    private int mRvHeight;
    private int mButtonHeight;
    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener()
    {
        @Override
        public void onAnimationStart(Animation animation)
        {

        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            final WindowManager.LayoutParams lp = (WindowManager.LayoutParams)getLayoutParams();
            lp.height = calcHeight();
            post(new Runnable() {

                public void run() {
                    updateViewState(true);
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation)
        {

        }
    };

    private IOverlayListener mOverlayListener;

    public OverlayView(Context context, OverlayLoggingSetup setup, int minimumVisibleLogPriority, IOverlayListener overlayListener)
    {
        super(context);

        mOverlayListener = overlayListener;

        mSetup = setup;

        mExpanded = mSetup.getWithStartExpanded();
        mRvHeight = dpToPx(getContext(), mSetup.getOverlayRecyclerViewHeight());
        mButtonHeight = dpToPx(getContext(), 36);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        // Create layout and get views
        View view = LayoutInflater.from(context).inflate(R.layout.overlay, null, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvLogs);
        mLabel = (TextView)view.findViewById(R.id.tvLabel);
        mLabelErrors = (TextView)view.findViewById(R.id.tvLabelError);
        mLLHeader = (LinearLayout)view.findViewById(R.id.llHeader);
        mCloseButton = (ImageView)view.findViewById(R.id.ivClose);
        mCollapseExpandButton = (ImageView)view.findViewById(R.id.ivCollapseExpand);
        mPauseButton = (ImageView)view.findViewById(R.id.ivPause);
        mFilterButton = (TextView) view.findViewById(R.id.tvFilter);
        mLLFilters = (LinearLayout)view.findViewById(R.id.llFilter);
        mVerboseButton = (TextView) view.findViewById(R.id.tvVerbose);
        mDebugButton = (TextView) view.findViewById(R.id.tvDebug);
        mInfoButton = (TextView) view.findViewById(R.id.tvInfo);
        mWarnButton = (TextView) view.findViewById(R.id.tvWarn);
        mErrorButton = (TextView) view.findViewById(R.id.tvError);

        // Setup label views
        mLabel.setBackgroundColor(setup.getBackgroundColor());
        mLabelErrors.setBackgroundColor(setup.getBackgroundColor());

        // Setup buttons and set listeners
        mCollapseExpandButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mExpanded = !mExpanded;
                updateRecyclerViewHeight(true);
                updateViewState(true);
                updateExpandButtonIcon();
            }
        });
        updateExpandButtonIcon();
        updateFilterButtonIcon(minimumVisibleLogPriority);
        updateFilterButtonsHeight(false);
        mFilterButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFilterShown = !mFilterShown;
                updateFilterButtonsHeight(true);
                updateViewState(true);
            }
        });
        mCloseButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOverlayListener.onClose();
            }
        });
        mPauseButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOverlayListener.onPause();
            }
        });
        OnClickListener filterClickListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int priority = Log.VERBOSE;
                if (v.getId() == R.id.tvVerbose)
                    priority = Log.VERBOSE;
                else if (v.getId() == R.id.tvDebug)
                    priority = Log.DEBUG;
                else if (v.getId() == R.id.tvInfo)
                    priority = Log.INFO;
                else if (v.getId() == R.id.tvWarn)
                    priority = Log.WARN;
                else if (v.getId() == R.id.tvError)
                    priority = Log.ERROR;
                mOverlayListener.onFilterChanged(priority);
                updateErrorFilter(priority);
                mFilterShown = false;
                updateFilterButtonsHeight(true);
                updateViewState(true);
            }
        };
        mVerboseButton.setOnClickListener(filterClickListener);
        mDebugButton.setOnClickListener(filterClickListener);
        mInfoButton.setOnClickListener(filterClickListener);
        mWarnButton.setOnClickListener(filterClickListener);
        mErrorButton.setOnClickListener(filterClickListener);

        // Setup RecyclerView
        mAdapter = new LogAdapter(minimumVisibleLogPriority);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setBackgroundColor(setup.getBackgroundColor());
        mRecyclerView.setAdapter(mAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    updateLabels(null);
            }
        });
        updateRecyclerViewHeight(false);

        // Add view
        addView(view);

        // Attach and display View
        mWindowManager.addView(this, calcWindowParams(false));
    }

    private void updateRecyclerViewHeight(boolean animate)
    {
        int expandedHeight = dpToPx(mRecyclerView.getContext(), mSetup.getOverlayRecyclerViewHeight());
//        int rvHeight = expandedHeight;
        int rvHeight = mExpanded ? expandedHeight : 0;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams();
        lp.height = rvHeight;
        mRecyclerView.setLayoutParams(lp);

//        if (animate)
//        {
//            if (mExpanded)
//                updateViewState(true);
//
//            TranslateAnimation translate1 = new TranslateAnimation(0f, 0f, mExpanded ? expandedHeight : 0f, mExpanded ? 0f : expandedHeight);
//            translate1.setFillAfter(false);
//            translate1.setDuration(500);
//            mRecyclerView.startAnimation(translate1);
//            TranslateAnimation translate2 = new TranslateAnimation(0f, 0f, mExpanded ? expandedHeight : 0f, mExpanded ? 0f : expandedHeight);
//            translate2.setFillAfter(false);
//            translate2.setDuration(500);
//            if (!mExpanded)
//                translate2.setAnimationListener(mAnimationListener);
//            mLLHeader.startAnimation(translate2);
//        }
    }

    private void updateFilterButtonsHeight(boolean animate)
    {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mLLFilters.getLayoutParams();
        lp.height = mFilterShown ? RelativeLayout.LayoutParams.WRAP_CONTENT : 0;
        mLLFilters.setLayoutParams(lp);
    }

    private void updateLabels(Integer index)
    {
        if (index == null)
            index = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        mLabel.setText(mLabel.getContext().getString(R.string.lumberjack_overlay_label, index + 1,  mAdapter.getItemCount()));
        mLabelErrors.setVisibility(mErrors > 0 ? View.VISIBLE : View.GONE);
        mLabelErrors.setText(mErrors > 0 ? mLabelErrors.getContext().getString(R.string.lumberjack_overlay_label_errors, mErrors) : "");
    }

    public void checkOrientation(int orientation)
    {
        updateViewState(true);
    }

    private void updateExpandButtonIcon()
    {
        mCollapseExpandButton.setImageResource(mExpanded ? R.drawable.ic_collapse_circle : R.drawable.ic_expand_circle);
    }

    private void updateFilterButtonIcon(int minimumVisibleLogPriority)
    {
        String label = "";
        switch (minimumVisibleLogPriority)
        {
            case Log.VERBOSE:
                label = "V";
                break;
            case Log.DEBUG:
                label = "D";
                break;
            case Log.INFO:
                label = "I";
                break;
            case Log.WARN:
                label = "W";
                break;
            case Log.ERROR:
                label = "E";
                break;
        }
        mFilterButton.setText(label);
    }

    private void updateViewState(boolean reuseParams)
    {
        mWindowManager.updateViewLayout(this, calcWindowParams(reuseParams));
    }

    private WindowManager.LayoutParams calcWindowParams(boolean reuseParams)
    {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams)getLayoutParams();
        if (!reuseParams)
        {
            lp = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    calcHeight(),//ViewGroup.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
            lp.x = 0;
            lp.y = 0;

            // Deactivate animations
            if (true)
            {
                lp.windowAnimations = 0;
                Field field = null;
                try
                {
                    field = WindowManager.LayoutParams.class.getDeclaredField("privateFlags");
                    field.setAccessible(true);
                    int flag = field.getInt(lp);
                    flag |= 0x00000040;
                    field.set(lp, flag);
                }
                catch (NoSuchFieldException e)
                {
                }
                catch (IllegalAccessException e)
                {
                }
            }
        }
        else
            lp.height = calcHeight();

        return lp;
    }

    private int calcHeight()
    {
        return (mExpanded ? mRvHeight + mButtonHeight : mButtonHeight) + (mFilterShown ? mButtonHeight * 5 : 0);
    }

    private int dpToPx(Context context, int dp)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160);
    }

    void addMessage(OverlayLoggingTree.LogEntry msg)
    {
        mErrors += (msg.getPriority() >= Log.ERROR ? 1 : 0);
        if (mAdapter.add(msg))
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        updateLabels(mAdapter.getItemCount() - 1);
    }

    void hideView()
    {
        mWindowManager.removeView(this);
    }

    void showView()
    {
        mWindowManager.addView(this, calcWindowParams(true));
    }

    void updateErrorFilter(int minimumVisibleLogPriority)
    {
        mAdapter.setFiltered(minimumVisibleLogPriority);
        updateLabels(mAdapter.getItemCount() - 1);
        updateFilterButtonIcon(minimumVisibleLogPriority);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    public interface IOverlayListener
    {
        void onFilterChanged(int priority);
        void onClose();
        void onPause();
    }
}
