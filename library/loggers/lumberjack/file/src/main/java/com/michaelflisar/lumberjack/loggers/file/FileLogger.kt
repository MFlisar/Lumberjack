package com.michaelflisar.lumberjack.loggers.file

import android.util.Log
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class FileLogger(
    val setup: FileLoggerSetup,
    private val timeToKeepFileOpen: Long = 2 * 60 * 1000L, // 2mins
    override var enabled: Boolean = true,
    override val filter: LumberjackFilter = DefaultLumberjackFilter
) : ILumberjackLogger {

    private val TAG = "FILELOGGER"
    private var file: File? = null
    private var bufferWriter: BufferedWriter? = null
    private val loggerScope = CoroutineScope(Dispatchers.IO + Job())
    private val channel = Channel<Event>()
    private var closeBufferJob: Job? = null

    init {
        loggerScope.launch {
            for (op in channel) {
                when (op) {
                    is Event.Data -> processWriteLog(op)
                    Event.ResetWriter -> reset()
                }
            }
        }
    }

    private fun send(event: Event) {
        loggerScope.launch {
            channel.send(event)
        }
    }

    override fun log(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ) {
        val text = setup.fileConverter.formatLog(
            level,
            tag,
            time,
            fileName,
            className,
            methodName,
            line,
            msg,
            throwable
        )
        send(event = Event.Data(text, time))
    }

    private suspend fun reset() {
        if (bufferWriter == null)
            return
        withContext(Dispatchers.IO) {
            bufferWriter?.close()
            bufferWriter = null
            file = null
        }
        L.d { "FileLogger reset" }
    }

    private suspend fun processWriteLog(data: Event.Data) {
        withContext(Dispatchers.IO) {
            // try/catch  - in no circumstance we want that any problem crashes the app because of the logger
            try {
                val path = setup.filePath(data.time)
                if (path != file?.path || bufferWriter == null) {
                    bufferWriter?.close()
                    file = File(path)
                    file?.parentFile?.mkdirs()
                    bufferWriter = BufferedWriter(FileWriter(file, true))
                }
                val writer = bufferWriter!!

                writer.write(data.log + "\n")
                writer.flush()
                setup.onLogged(loggerScope)

                closeBufferJob?.cancelAndJoin()
                closeBufferJob = createCloseBufferJob()
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't write the log: '${data.log}'", e)
            }
        }
    }

    private fun createCloseBufferJob(): Job {
        return loggerScope.launch(Dispatchers.IO) {
            delay(timeToKeepFileOpen)
            if (isActive) {
                bufferWriter?.close()
                bufferWriter = null
            }
        }
    }

    internal fun onLogFilesWillBeDeleted(files: List<File>) {
        file?.let {
            if (files.contains(it)) {
                if (file?.exists() == true) {
                    send(Event.ResetWriter)
                    loggerScope.launch {
                        channel.send(Event.ResetWriter)
                    }
                }
            }
        }
    }

    sealed class Event {
        data class Data(val log: String, val time: Long) : Event()
        data object ResetWriter : Event()
    }

}