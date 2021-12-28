package com.michaelflisar.lumberjack.interfaces

import com.michaelflisar.lumberjack.data.StackData

interface IFormatter {

    /*
     * defines how the prefix of a log line does look likelogs
     *
     * by default, it looks like following:
     * [MainActivity:26 onCreate]
     * [<CUSTOM TAG> MainActivity:26 onCreate]
     */
    fun formatLogPrefix(customTag: String?, stackData: StackData) : String

}