package com.michaelflisar.lumberjack.interfaces

import android.os.Parcelable
import com.michaelflisar.lumberjack.core.Level

interface IDataExtractor : Parcelable {

    fun extract(line: Int, fullLogLine: String): Data?

    class Data(
        val line: Int,
        val fullLogLine: String,
        val level: Level,
        val date: String,
        val log: String)
}