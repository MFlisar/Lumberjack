package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.implementation.platformPrintln
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

class FileLogger(
    val setup: FileLoggerSetup,
    private val timeToKeepFileOpen: Long = 2 * 60 * 1000L, // 2mins
    override var enabled: Boolean = true,
    override val filter: LumberjackFilter = DefaultLumberjackFilter
) : ILumberjackLogger {

    private val TAG = "FILELOGGER"
    private var file: Path? = null
    private var bufferWriter: Sink? = null
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
                val path = Path(setup.filePath(data))
                if (path != file || bufferWriter == null) {
                    bufferWriter?.close()
                    file = path
                    file?.parent?.let {
                        SystemFileSystem.createDirectories(it)
                    }
                    bufferWriter = SystemFileSystem.sink(file!!, true).buffered()
                }
                val writer = bufferWriter!!

                writer.writeString(data.log + "\n")
                writer.flush()
                setup.onLogged(loggerScope)

                closeBufferJob?.cancelAndJoin()
                closeBufferJob = createCloseBufferJob()
            } catch (e: Exception) {
                platformPrintln(
                    "",
                    Level.ERROR,
                    TAG,
                    "Couldn't write the log: '${data.log}' (${e.message})"
                )
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

    internal fun onLogFilesWillBeDeleted(files: List<Path>) {
        file?.let {
            if (files.contains(it)) {
                if (SystemFileSystem.exists(it)) {
                    send(Event.ResetWriter)
                    println("onLogFilesWillBeDeleted...")
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