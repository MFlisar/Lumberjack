package com.michaelflisar.lumberjack.overlay;

import android.content.Context;
import android.graphics.Color;
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

/**
 * Created by flisar on 13.02.2017.
 */

class OverlayView extends FrameLayout
{
    private OverlayLoggingSetup mSetup;
    private ImageView mCloseButton;
    private ImageView mCollapseExpandButton;
    private ImageView mPauseButton;
    private ImageView mErrorButton;
    private TextView mLabel;
    private TextView mLabelErrors;
    private RecyclerView mRecyclerView;
    private WindowManager mWindowManager;
    private int mErrors = 0;
    private LogAdapter mAdapter;
    private boolean mExpanded = true;
    private int mMinimumLogPriority;
    private boolean mShowErrorsOnly;

    public OverlayView(Context context, OverlayLoggingSetup setup, boolean showErrorsOnly)
    {
        super(context);

        mSetup = setup;

        mExpanded = mSetup.getWithStartExpanded();
        mMinimumLogPriority = mSetup.getLogPriorityForErrorFilter();
        mShowErrorsOnly = showErrorsOnly;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int buttonHeight = dpToPx(context, 36);
        int desiredLayoutHeight = dpToPx(context, setup.getOverlayHeight());
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        // Create layout
        View view = LayoutInflater.from(context).inflate(R.layout.overlay, null, false);
        mCloseButton = (ImageView)view.findViewById(R.id.btClose);
        mCollapseExpandButton = (ImageView)view.findViewById(R.id.btCollapseExpand);
        mPauseButton = (ImageView)view.findViewById(R.id.btPause);
        mErrorButton = (ImageView)view.findViewById(R.id.btErrors);
        mCollapseExpandButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mExpanded = !mExpanded;
                updateViewState();
                updateExpandButtonIcon();
            }
        });
        updateExpandButtonIcon();
        updateErrorButtonIcon(showErrorsOnly);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvLogs);
        mLabel = (TextView)view.findViewById(R.id.tvLabel);
        mLabelErrors = (TextView)view.findViewById(R.id.tvLabelError);
        mLabel.setBackgroundColor(setup.getBackgroundColor());
        mLabelErrors.setBackgroundColor(setup.getBackgroundColor());

        // Setup RecyclerView
        mAdapter = new LogAdapter(mMinimumLogPriority, showErrorsOnly);
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
        mWindowManager.addView(this, calcWindowParams());
    }

    private void updateLabels(Integer index)
    {
        if (index == null)
            index = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        mLabel.setText(mLabel.getContext().getString(mShowErrorsOnly ? R.string.lumberjack_overlay_label_errors_only : R.string.lumberjack_overlay_label, index + 1,  mAdapter.getItemCount()));
        mLabelErrors.setVisibility(!mShowErrorsOnly && mErrors > 0 ? View.VISIBLE : View.GONE);
        mLabelErrors.setText(mErrors > 0 ? mLabelErrors.getContext().getString(R.string.lumberjack_overlay_label_errors, mErrors) : "");
    }

    public void checkOrientation(int orientation)
    {
        updateViewState();
    }

    private void updateExpandButtonIcon()
    {
        mCollapseExpandButton.setImageResource(mExpanded ? R.drawable.ic_collapse_circle : R.drawable.ic_expand_circle);
    }

    private void updateErrorButtonIcon(boolean showErrorsOnly)
    {
        mErrorButton.setImageResource(showErrorsOnly ? R.drawable.ic_error_outline_circle : R.drawable.ic_error_outline_circle_disabled);
    }

    private void updateViewState()
    {
        mWindowManager.updateViewLayout(this, calcWindowParams());
        mRecyclerView.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
    }

    private WindowManager.LayoutParams calcWindowParams()
    {
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int buttonHeight = dpToPx(getContext(), 36);
        int desiredLayoutHeight = dpToPx(getContext(), mSetup.getOverlayHeight());
        if (!mExpanded)
            desiredLayoutHeight = buttonHeight;
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = 0;
        windowParams.y = windowDimen.y - layoutHeight;

        return windowParams;
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
        mWindowManager.addView(this, calcWindowParams());
    }

    void updateErrorFilter(boolean showErrorsOnly)
    {
        mShowErrorsOnly = showErrorsOnly;
        mAdapter.setFiltered(showErrorsOnly);
        updateLabels(mAdapter.getItemCount() - 1);
        updateErrorButtonIcon(showErrorsOnly);
    }

    View getCloseButton()
    {
        return mCloseButton;
    }

    View getErrorButton()
    {
        return mErrorButton;
    }

    View getPauseButton()
    {
        return mPauseButton;
    }
}
