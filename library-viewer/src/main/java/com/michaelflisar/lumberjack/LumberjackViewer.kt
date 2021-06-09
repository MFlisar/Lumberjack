package com.michaelflisar.lumberjack

import android.content.Context
import android.content.Intent
import com.michaelflisar.lumberjack.interfaces.ILumberjackViewActivityProvider
import java.io.File

object LumberjackViewer : ILumberjackViewActivityProvider {

    const val FILE = "FILE"

    fun show(context: Context, file: File) {
        context.startActivity(createIntent(context, file))
    }

    override fun createIntent(context: Context, file: File) : Intent {
        return Intent(
            context,
            LumberjackViewerActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(FILE, file)
        }
    }

}