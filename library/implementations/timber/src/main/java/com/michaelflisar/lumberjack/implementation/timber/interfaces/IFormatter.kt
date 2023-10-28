package com.michaelflisar.lumberjack.implementation.timber.interfaces

import com.michaelflisar.lumberjack.implementation.timber.data.StackData
import timber.log.BaseTree

interface IFormatter {

    /*
     * defines how the prefix and the message are printed
     *
     * by default, it looks like following:
     * PREFIX: MESSAGE
     */
    fun formatLine(tree: BaseTree, prefix: String, message: String): String

    /*
     * defines how the prefix of a log line does look like
     *
     * by default, it looks like following:
     * [MainActivity:26 onCreate]
     * [<LUMBERJACK TAG> MainActivity:26 onCreate]
     */
    fun formatLogPrefix(lumberjackTag: String?, stackData: StackData): String

}