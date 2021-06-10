package com.michaelflisar.lumberjack.interfaces

import android.content.Context
import android.content.Intent
import java.io.File

interface ILumberjackViewActivityProvider {
    fun createIntent(context: Context, fileLoggingSetup: IFileLoggingSetup): Intent
    fun createIntent(context: Context, fileLoggingSetup: IFileLoggingSetup, dataExtractor: IDataExtractor): Intent
}