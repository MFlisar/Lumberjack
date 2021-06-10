package com.michaelflisar.lumberjack.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.michaelflisar.lumberjack.DefaultDataExtractor
import com.michaelflisar.lumberjack.interfaces.IDataExtractor
import com.michaelflisar.lumberjack.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.interfaces.ILumberjackViewActivityProvider

object LumberjackViewer : ILumberjackViewActivityProvider {

    const val FILE_LOGGING_SETUP = "FILE-LOGGING_SETUP"
    const val DATA_EXTRACTOR = "DATA-EXTRACTOR"

    fun show(
        context: Context,
        fileLoggingSetup: IFileLoggingSetup,
        dataExtractor: IDataExtractor = DefaultDataExtractor
    ) {
        context.startActivity(createIntent(context, fileLoggingSetup, dataExtractor))
    }

    override fun createIntent(context: Context, fileLoggingSetup: IFileLoggingSetup): Intent {
        return createIntent(context, fileLoggingSetup, DefaultDataExtractor)
    }

    override fun createIntent(
        context: Context,
        fileLoggingSetup: IFileLoggingSetup,
        dataExtractor: IDataExtractor
    ): Intent {
        return Intent(
            context,
            LumberjackViewerActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(FILE_LOGGING_SETUP, fileLoggingSetup)
            putExtra(DATA_EXTRACTOR, dataExtractor)
        }
    }
}