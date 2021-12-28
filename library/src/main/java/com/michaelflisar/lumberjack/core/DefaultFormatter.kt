package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.data.StackData
import com.michaelflisar.lumberjack.interfaces.IFormatter

open class DefaultFormatter : IFormatter {

    override fun formatLogPrefix(customTag: String?, stackData: StackData) : String {
        return if (customTag != null) {
            "[<$customTag> ${getStackTag(stackData)}]"
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