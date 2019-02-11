package com.michaelflisar.lumberjack

import com.michaelflisar.lumberjack.data.TimerData
import java.util.*

/**
 * Created by Michael on 17.01.2019.
 */

object T {
    private val mTimers = HashMap<Any, TimerData>()
    private val mEmptyData = TimerData()

    // ------------------
    // Simple stop watch like time logging
    // ------------------

    /**
     * start a time log accessable (and bound to) the provided key
     * and will reset any already existing data bound to that key
     *
     * @param key the key that this timer should be bound to
     */
    fun start(key: Any) {
        clear(key)
        mTimers[key] = TimerData().start()
    }

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration or null, if key does not exist or timer was stopped already
     */
    fun lap(key: Any): Long? {
        val data = getTimer(key)
        return data.lap()
    }

    /**
     * stops a timer, afterwards you can't add laps any more and the end time is saved as well
     *
     * @param key the key that the desired timer is bound to
     * @return the total duration or null, if key does not exist or timer was stopped already
     */
    fun stop(key: Any): Long? {
        val data = getTimer(key)
        return data.stop()
    }

    /**
     * Clears all data that exists for a given key
     *
     * @param key the key that the desired timer is bound to
     */
    fun clear(key: Any) {
        mTimers.remove(key)
    }

    /**
     * Checks if a timer is existing
     *
     * @param key the key that the desired timer is bound to
     */
    fun exists(key: Any): Boolean {
        return mTimers.containsKey(key)
    }

    // ------------------
    // Convenient action functions with pretty result print as result
    // ------------------

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration as a readable string or null, if key does not exist or timer was stopped already
     */
    fun printAndLap(key: Any): String {
        val lap = lap(key) ?: return "NULL"
        return "Lap = " + lap + "ms"
    }

    /**
     * stops a timer
     *
     * @param key the key that the desired timer is bound to
     * @return the total duration as a readable string or null, if key does not exist or timer was stopped already
     */
    fun printAndStop(key: Any): String {
        val stop = stop(key) ?: return "NULL"
        return "Total = " + stop + "ms"
    }

    /**
     * adds a lap to the timer bound to the provider key
     *
     * @param key the key that the desired timer is bound to
     * @return the last laps duration and the total duration as a readable string or null, if key does not exist or timer was stopped already
     */
    fun printAndLapTotal(key: Any): String {

        val lap = lap(key) ?: return "NULL"
        val data = getTimer(key)
        val total = data.getTotal() ?: "NULL"
        return "Total = " + total + "ms | Lap = " + lap + "ms"
    }

    fun print(key: Any): String {
        if (!exists(key)) {
            return "Timer[$key] does not exist!"
        }

        val data = getTimer(key)
        var info = "Started: ${data.wasStarted()}"
        data.getLaps()?.size?.let {
            info += " | Laps: ${it}"
        }
        info += " | Total = ${data.getTotal()}ms | Running: ${data.isRunning()}"
        return info
    }

// ------------------
// getters
// ------------------

    fun getStart(key: Any): Long? {
        val data = mTimers[key]
        return data?.getStart()
    }

    fun getLaps(key: Any): List<Long>? {
        val data = mTimers[key]
        return data?.getLaps()
    }

    fun getEnd(key: Any): Long? {
        val data = mTimers[key]
        return data?.getEnd()
    }

// ------------------
// internal functions - null save
// ------------------

    private fun getTimer(key: Any): TimerData = mTimers[key] ?: mEmptyData
}