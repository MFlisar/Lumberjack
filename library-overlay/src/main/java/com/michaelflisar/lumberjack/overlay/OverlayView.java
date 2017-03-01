package com.michaelflisar.lumberjack.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.OverlayLoggingTree;

import java.lang.reflect.Field;

/**
 * Created by flisar on 13.02.2017.
 */

class OverlayView extends FrameLayout
{
    private OverlayLoggingSetup mSetup;
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

    private IOverlayListener mOverlayListener;

    public OverlayView(Context context, OverlayLoggingSetup setup, int minimumVisibleLogPriority, IOverlayListener overlayListener)
    {
        super(context);

        mOverlayListener = overlayListener;

        mSetup = setup;

        mExpanded = mSetup.getWithStartExpanded();

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int buttonHeight = dpToPx(context, 36);
        int desiredLayoutHeight = dpToPx(context, setup.getOverlayHeight());
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        // Create layout and get views
        View view = LayoutInflater.from(context).inflate(R.layout.overlay, null, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvLogs);
        mLabel = (TextView)view.findViewById(R.id.tvLabel);
        mLabelErrors = (TextView)view.findViewById(R.id.tvLabelError);
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
                updateViewState(false);
                updateExpandButtonIcon();
            }
        });
        updateExpandButtonIcon();
        updateFilterButtonIcon(minimumVisibleLogPriority);
        mLLFilters.setVisibility(View.GONE);
        mFilterButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mLLFilters.setVisibility(mLLFilters.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
                mLLFilters.setVisibility(View.GONE);
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
        mRecyclerView.getLayoutParams().height = layoutHeight - buttonHeight;
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
        mRecyclerView.setVisibility(mExpanded ? View.VISIBLE : View.GONE);

        // Add view
        addView(view);

        // Attach and display View
        mWindowManager.addView(this, calcWindowParams(false));
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
        updateViewState(false);
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

    private void updateViewState(boolean disableAnimations)
    {
        mRecyclerView.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
        mWindowManager.updateViewLayout(this, calcWindowParams(disableAnimations));
    }

    private WindowManager.LayoutParams calcWindowParams(boolean disableAnimations)
    {
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int buttonHeight = dpToPx(getContext(), 36);
        int desiredLayoutHeight = dpToPx(getContext(), mSetup.getOverlayHeight());
        if (!mExpanded)
            desiredLayoutHeight = buttonHeight;
        if (mLLFilters.getVisibility() == View.VISIBLE)
            desiredLayoutHeight += 5 * buttonHeight;
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                layoutHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = 0;
        lp.y = windowDimen.y - layoutHeight;

        // Deactivate animations
        if (disableAnimations)
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
            } catch (NoSuchFieldException e)
            {
            } catch (IllegalAccessException e)
            {
            }
        }

        return lp;
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
        mWindowManager.addView(this, calcWindowParams(false));
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
