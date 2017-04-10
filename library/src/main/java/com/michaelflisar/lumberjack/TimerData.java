package com.michaelflisar.lumberjack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flisar on 09.03.2017.
 */

public class TimerData
{
    private long mStart;
    private long mEnd;

    private List<Long> mLaps;

    public TimerData()
    {
        mStart = 0;
        mEnd = 0;
        mLaps = null;
    }

    // -------------
    // functions
    // -------------

    TimerData start()
    {
        if (!isRunning())
            mStart = System.currentTimeMillis();
        return this;
    }

    Long stop()
    {
        if (!isRunning() || isEnded())
            return null;

        mEnd = System.currentTimeMillis();
        return mEnd - mStart;
    }

    Long lap()
    {
        if (!isRunning())
            return null;

        if (mLaps == null)
            mLaps = new ArrayList<>();

        long lap = System.currentTimeMillis();
        mLaps.add(lap);
        return lap - (mLaps.size() == 1 ? mStart : mLaps.get(mLaps.size() - 2));
    }

    // -------------
    // getter
    // -------------

    Long getStart()
    {
        if (!isRunning())
            return null;

        return mStart;
    }

    Long getEnd()
    {
        if (!isRunning() || !isEnded())
            return null;

        return mEnd;
    }

    List<Long> getLaps()
    {
        return mLaps;
    }

    Long getLastLapTotal()
    {
        if (!isRunning() || mLaps == null)
            return null;
        return mLaps.get(mLaps.size() - 1) - mStart;
    }

    // -------------
    // helper functions
    // -------------

    private boolean isRunning()
    {
        return mStart != 0;
    }

    private boolean isEnded()
    {
        return mEnd != 0;
    }
}
