package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.data.StackData
import com.michaelflisar.lumberjack.interfaces.IFormatter
import timber.log.BaseTree
import timber.log.ConsoleTree

open class DefaultFormatter : IFormatter {

    override fun formatLine(tree: BaseTree, prefix: String, message: String) : String {
        return if (tree is ConsoleTree) {
            // tag is logged anyways inside the console, so we do NOT add it to the message!
            message
        } else {
            "$prefix: $message"
        }
    }

    override fun formatLogPrefix(lumberjackTag: String?, stackData: StackData) : String {
        return if (lumberjackTag != null) {
            "[<$lumberjackTag> ${getStackTag(stackData)}]"
        } else {
            "[${getStackTag(stackData)}]"
        }
    }

    protected open fun getStackTag(stackData: StackData): String {
        val simpleClassName = formatClassName(stackData.className)
        var tag = "$simpleClassName:${stackData.element?.lineNumber} ${stackData.element?.methodName}"
        stackData.element2?.let {
            val simpleClassName2 = formatClassName(stackData.className2)
            val extra = simpleClassName2.replace(simpleClassName, "")
            tag += " ($extra:${it.lineNumber})"
        }
        return tag
    }

    protected open fun formatClassName(className: String) : String {
        return className.substring(className.lastIndexOf('.') + 1)
    }
}