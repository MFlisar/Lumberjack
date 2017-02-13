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
    private List<OverlayLoggingTree.LogEntry> mItems;

    public LogAdapter()
    {
        mItems = new ArrayList<>();
    }

    public void add(OverlayLoggingTree.LogEntry item)
    {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
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
        OverlayLoggingTree.LogEntry item = mItems.get(position);
        holder.text.setText(item.getMessage());
        holder.text.setTextColor(item.getColor());
        holder.text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, item.getTextSizeInDp());
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
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