package com.michaelflisar.lumberjack.core.interfaces

import com.michaelflisar.lumberjack.core.CommonParcelable
import com.michaelflisar.lumberjack.core.classes.Level

interface IFileConverter : CommonParcelable {

    fun parseFile(lines: List<String>) : List<Entry>
    fun formatLog(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ) : String

    class Entry(
        val lineNumber: Int,
        var lines: MutableList<String>,
        val level: Level,
        val date: String
    )
}