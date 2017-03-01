package com.michaelflisar.lumberjack.overlay;

import android.graphics.Color;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelflisar.lumberjack.OverlayLoggingTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flisar on 13.02.2017.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>
{
    private int mMinimumVisiblePriority;

    private List<OverlayLoggingTree.LogEntry> mItems;
    private List<OverlayLoggingTree.LogEntry> mFilteredItems;

    public LogAdapter(int mimimumVisiblePriority)
    {
        mMinimumVisiblePriority = mimimumVisiblePriority;
        mItems = new ArrayList<>();
        mFilteredItems = new ArrayList<>();
    }

    public void setFiltered(int mimimumVisiblePriority)
    {
        if (mMinimumVisiblePriority != mimimumVisiblePriority)
        {
            mMinimumVisiblePriority = mimimumVisiblePriority;
            calcFilteredItems();
            notifyDataSetChanged();
        }
    }

    private void calcFilteredItems()
    {
        mFilteredItems.clear();
        for (OverlayLoggingTree.LogEntry item : mItems)
        {
            if (item.getPriority() >= mMinimumVisiblePriority)
                mFilteredItems.add(item);
        }
    }

    public boolean add(OverlayLoggingTree.LogEntry item)
    {
        mItems.add(item);
        if (item.getPriority() >= mMinimumVisiblePriority)
        {
            mFilteredItems.add(item);
            notifyItemInserted(mFilteredItems.size() - 1);
        }
        else
            return false;
        return true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        OverlayLoggingTree.LogEntry item = mFilteredItems.get(position);
        holder.text.setText(item.getMessage());
        holder.text.setTextColor(item.getColor());
        holder.text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, item.getTextSizeInDp());
    }

    @Override
    public int getItemCount()
    {
        return mFilteredItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView text;

        public ViewHolder(View itemView)
        {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}