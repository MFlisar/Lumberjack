package com.michaelflisar.lumberjack.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by flisar on 17.01.2019.
 */

class TimerData {

    companion object {
        val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    }

    private var mStart: Long = 0
    private var mEnd: Long = 0
    private var mLaps: MutableList<Long>? = null

    // -------------
    // getter
    // -------------

    fun getStart(): Long? = if (wasStarted()) mStart else null

    fun getEnd(): Long? = if (wasEnded()) null else mEnd

    fun getLaps(): List<Long>? = mLaps

    fun getLastLapTotal(): Long? = if (wasStarted() || mLaps == null) null else mLaps!![mLaps!!.size - 1] - mStart

    fun getTotal(): Long? = if (wasEnded()) mEnd - mStart else (if (wasStarted()) System.currentTimeMillis() - mStart else null)

    fun getStartTime() : String = if (wasStarted()) TIME_FORMATTER.format(mStart) else "NOT STARTED"

    // -------------
    // helper functions
    // -------------

    /*
     returns if timer was started, even after stop is called
     */
    fun wasStarted() = mStart != 0L

    /*
     returns if timer is running
     will be true after starting until you call stop
     */
    fun isRunning() = mStart != 0L && mEnd == 0L

    /*
     returns if timer was started and stopped and is not running anymore
     */
    fun wasEnded() = wasStarted() && mEnd != 0L

    // -------------
    // functions
    // -------------

    fun start(): TimerData {
        if (!isRunning())
            mStart = System.currentTimeMillis()
        return this
    }

    fun stop(): Long? {
        if (!isRunning() || wasEnded())
            return null

        mEnd = System.currentTimeMillis()
        return mEnd - mStart
    }

    fun lap(): Long? {
        if (!isRunning())
            return null

        if (mLaps == null)
            mLaps = ArrayList()

        val lap = System.currentTimeMillis()
        mLaps!!.add(lap)
        return lap - if (mLaps!!.size == 1) mStart else mLaps!![mLaps!!.size - 2]
    }
}
