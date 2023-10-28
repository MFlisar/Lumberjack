package com.michaelflisar.lumberjack.implementation.timber.interfaces

import timber.log.BaseTree

interface IFilter {
    fun isTagEnabled(baseTree: BaseTree, tag: String): Boolean
    fun isPackageNameEnabled(packageName: String): Boolean
}