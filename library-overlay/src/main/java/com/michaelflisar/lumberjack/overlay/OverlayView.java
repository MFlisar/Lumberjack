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
    private TextView mLabel;
    private RecyclerView mRecyclerView;
    private WindowManager mWindowManager;
    private LogAdapter mAdapter;

    public OverlayView(Context context, OverlayLoggingSetup setup)
    {
        super(context);

        mSetup = setup;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int desiredLayoutHeight = dpToPx(context, setup.getOverlayHeight());
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        // Create layout
        View view = LayoutInflater.from(context).inflate(R.layout.overlay, null, false);
        mCloseButton = (ImageView)view.findViewById(R.id.btClose);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvLogs);
        mLabel = (TextView)view.findViewById(R.id.tvLabel);

        // Setup buttons
        int buttonHeight = dpToPx(context, 40);
        mCloseButton.getLayoutParams().height = buttonHeight;

        mLabel.setBackgroundColor(setup.getBackgroundColor());

        // Setup RecyclerView
        mAdapter = new LogAdapter();
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
                    updateLabel(null);
            }
        });

        // Add view
        addView(view);

        // Set View parameters
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = 0;
        windowParams.y = windowDimen.y - layoutHeight;

        // Attach and display View
        mWindowManager.addView(this, windowParams);
    }

    private void updateLabel(Integer index)
    {
        if (index == null)
            index = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        mLabel.setText(mLabel.getContext().getString(R.string.lumberjack_overlay_label, index + 1,  mAdapter.getItemCount()));
    }

    public void checkOrientation(Context context, int orientation)
    {
        Point windowDimen = new Point();
        mWindowManager.getDefaultDisplay().getSize(windowDimen);

        int desiredLayoutHeight = dpToPx(context, mSetup.getOverlayHeight());
        int layoutHeight = desiredLayoutHeight < windowDimen.y ? desiredLayoutHeight : windowDimen.y;

        // Set View parameters
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = 0;
        windowParams.y = windowDimen.y - layoutHeight;

        // Update view
        mWindowManager.updateViewLayout(this, windowParams);
    }

    private int dpToPx(Context context, int dp)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160);
    }

    void addMessage(OverlayLoggingTree.LogEntry msg)
    {
        mAdapter.add(msg);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        updateLabel(mAdapter.getItemCount() - 1);
    }

    void hideView()
    {
        mWindowManager.removeView(this);
    }

    View getCloseButton()
    {
        return mCloseButton;
    }
}
