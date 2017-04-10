package com.michaelflisar.lumberjack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Michael on 13.10.2016.
 */

public class T
{
    private static HashMap<Object, TimerData> mTimers = new HashMap<>();
    private static final TimerData mEmptyData = new TimerData();

    // ------------------
    // Simple stop watch like time logging
    // ------------------

    /**
     * start a time log accessable (and bound to) the provided key
     * and will reset any already existing data bound to that key
     *
     * @param key the key that this timer should be bound to
     */
    public static void start(Object key)
    {
        clear(key);
        mTimers.put(key, new TimerData().start());
    }

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration or null, if key does not exist or timer was stopped already
     */
    public static Long lap(Object key)
    {
        TimerData data = getTimer(key);
        return data.lap();
    }

    /**
     * stops a timer, afterwards you can't add laps any more and the end time is saved as well
     *
     * @param key the key that the desired timer is bound to
     * @return the total duration or null, if key does not exist or timer was stopped already
     */
    public static Long stop(Object key)
    {
        TimerData data = getTimer(key);
        return data.stop();
    }

    /**
     * Clears all data that exists for a given key
     *
     * @param key the key that the desired timer is bound to
     */
    public static void clear(Object key)
    {
        mTimers.remove(key);
    }

    /**
     * Checks if a timer is existing
     *
     * @param key the key that the desired timer is bound to
     */
    public static boolean exists(Object key)
    {
        return mTimers.containsKey(key);
    }

    // ------------------
    // Grouping functions
    // ------------------

    // TODO

    // ------------------
    // Convenient action functions with pretty result print as result
    // ------------------

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration as a readable string or null, if key does not exist or timer was stopped already
     */
    public static String printAndLap(Object key)
    {
        Long lap = lap(key);
        if (lap == null)
            return "NULL";
        return "Lap = " + lap + "ms";
    }

    /**
     * stops a timer
     *
     * @param key the key that the desired timer is bound to
     * @return the total duration as a readable string or null, if key does not exist or timer was stopped already
     */
    public static String printAndStop(Object key)
    {
        Long stop = stop(key);
        if (stop == null)
            return "NULL";
        return "Total = " + stop + "ms";
    }

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration and the total duration as a readable string or null, if key does not exist or timer was stopped already
     */
    public static String printAndLapTotal(Object key)
    {

        Long lap = lap(key);
        if (lap == null)
            return "NULL";
        TimerData data = getTimer(key);
        long total = data.getLastLapTotal();
        return "Total = " + total + "ms | Lap = " + lap + "ms";
    }

    // ------------------
    // getters
    // ------------------

    public static Long getStart(Object key)
    {
        TimerData data = mTimers.get(key);
        return data.getStart();
    }

    public static List<Long> getLaps(Object key)
    {
        TimerData data = mTimers.get(key);
        return data.getLaps();
    }

    public static Long getEnd(Object key)
    {
        TimerData data = mTimers.get(key);
        return data.getEnd();
    }

    // ------------------
    // internal functions - null save
    // ------------------

    private static final TimerData getTimer(Object key)
    {
        TimerData data = mTimers.get(key);
        if (data == null)
            return mEmptyData;
        return data;
    }
}