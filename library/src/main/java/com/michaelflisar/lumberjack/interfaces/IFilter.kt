package com.michaelflisar.lumberjack.interfaces

import timber.log.BaseTree

interface IFilter {
    fun isTagEnabled(baseTree: BaseTree, tag: String): Boolean
    fun isPackageNameEnabled(packageName: String): Boolean
}