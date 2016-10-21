package com.michaelflisar.lumberjack;

import android.util.Pair;

import java.util.HashMap;

/**
 * Created by Michael on 13.10.2016.
 */

public class T
{
    private static HashMap<Object, Long> mFirstTimestamps = new HashMap<>();
    private static HashMap<Object, Long> mLastTimestamps = new HashMap<>();

    public static void start(Object key)
    {
        long ts = System.currentTimeMillis();
        mFirstTimestamps.put(key, ts);
        mLastTimestamps.put(key, ts);
    }

    public static void clear(Object key)
    {
        mFirstTimestamps.remove(key);
        mLastTimestamps.remove(key);
    }

    public static long get(Object key)
    {
        long ts = System.currentTimeMillis();
        long first = mFirstTimestamps.get(key);
        if (first == 0)
            return 0;

        mLastTimestamps.put(key, ts);
        return ts - first;
    }

    public static long getLap(Object key)
    {
        long ts = System.currentTimeMillis();
        long last = mLastTimestamps.get(key);
        if (last == 0)
            return 0;

        mLastTimestamps.put(key, ts);
        return ts - last;
    }

    public static Pair<Long, Long> getTotalAndLap(Object key)
    {
        long ts = System.currentTimeMillis();
        long first = mFirstTimestamps.get(key);
        long last = mLastTimestamps.get(key);
        if (first == 0)
            return null;

        mLastTimestamps.put(key, ts);
        return new Pair<>(ts - first, ts - last);
    }

    public static String printTotalAndLap(Object key)
    {
        Pair<Long, Long> data = getTotalAndLap(key);
        if (data == null)
            return "NULL";

        return "Total = " + data.first + "ms | Lap = " + data.second + "ms";
    }

    public static String print(Object key)
    {
        long data = get(key);
        if (data == 0)
            return "NULL";

        return "Total = " + data + "ms";
    }

    public static String printLap(Object key)
    {
        long data = getLap(key);
        if (data == 0)
            return "NULL";

        return "Lap = " + data + "ms";
    }
}
