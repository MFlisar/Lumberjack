package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.implementation.platformPrintln
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.time.Duration.Companion.milliseconds

/**
 * File logger implementation that writes logs to a file on disk.
 *
 * @param setup Configuration for the file logger, including how to format logs and determine file paths.
 * @param timeToKeepFileOpen Duration in milliseconds to keep the log file open after the last log entry is written. Defaults to 2 minutes.
 * @param minLogLevel Minimum log level this logger will output. Defaults to [Level.VERBOSE].
 * @param filter Lumberjack filter used to decide which logs are allowed. Defaults to [DefaultLumberjackFilter] which does not filter out anything.
 */
class FileLogger(
    val setup: FileLoggerSetup,
    private val timeToKeepFileOpen: Long = 2 * 60 * 1000L, // 2mins
    override val minLogLevel: Level = Level.VERBOSE,
    override val filter: LumberjackFilter = DefaultLumberjackFilter,
) : ILumberjackLogger {

    sealed class Event {
        data class Log(val log: String, val time: Long) : Event()
        data object CloseWriter : Event()

        data class DeleteFiles(val deprecatedOnly: Boolean) : Event()
    }

    companion object {

        internal const val TAG = "FILELOGGER"

    }

    private var bufferWriter: Sink? = null
    private var currentPath: Path? = null
    private val loggerScope = CoroutineScope(Dispatchers.IO + Job())
    private val channel = Channel<Event>(Channel.UNLIMITED)
    private var closeBufferJob: Job? = null

    init {
        loggerScope.launch {
            for (op in channel) {
                when (op) {
                    is Event.Log -> processWriteLog(op)
                    is Event.CloseWriter -> closeWriter()
                    is Event.DeleteFiles -> {
                        cancelCloseBufferJob()
                        closeWriter()
                        cleanupFiles(op.deprecatedOnly)
                    }
                }
            }
        }
    }

    private fun send(event: Event) {
        channel.trySend(event)
    }

    fun deleteAllLogFiles() {
        send(event = Event.DeleteFiles(deprecatedOnly = false))
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
        throwable: Throwable?,
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
        send(event = Event.Log(text, time))
    }

    private suspend fun processWriteLog(event: Event.Log) {
        // try/catch  - in no circumstance we want that any problem crashes the app because of the logger
        try {

            cancelCloseBufferJob()

            val rollOverType = setup.getRollOverType(currentPath, event)
            val didRollOver = performRollOver(rollOverType)

            val writer = getOrCreateWriter(event)
            writer.writeString(event.log + "\n")
            writer.flush()

            // now we know that a new log file may have been created - now we can really
            // check the file count as well if needed...
            if (didRollOver)
                cleanupFiles(false)

            createCloseBufferJob()

        } catch (e: Exception) {
            platformPrintln(
                "",
                Level.ERROR,
                TAG,
                "Couldn't write the log: '${event.log}' (${e.message})"
            )
        }
    }

    private fun getOrCreateWriter(event: Event.Log): Sink {
        val path = setup.getActiveLogFilePath(event)
        if (bufferWriter == null) {
            path.parent?.let { SystemFileSystem.createDirectories(it) }
            bufferWriter = SystemFileSystem
                .sink(path, true)
                .buffered()
            currentPath = path
        }
        return bufferWriter!!
    }

    private fun closeWriter() {
        bufferWriter?.flush()
        bufferWriter?.close()
        bufferWriter = null
        currentPath = null
    }

    private fun performRollOver(
        rollOverType: BaseFileLoggerSetup.RollOverType,
    ): Boolean {
        return when (rollOverType) {
            BaseFileLoggerSetup.RollOverType.None -> {
                false
            }

            is BaseFileLoggerSetup.RollOverType.TrimFile -> {
                val path = currentPath!!
                closeWriter()

                trimFile(path, rollOverType.trimFactor)

                true
            }

            BaseFileLoggerSetup.RollOverType.SwitchFile -> {
                closeWriter()
                true
            }

            is BaseFileLoggerSetup.RollOverType.ArchiveFile -> {
                val sourcePath = currentPath!!
                closeWriter()
                SystemFileSystem.atomicMove(
                    sourcePath,
                    rollOverType.archivePath
                )
                true
            }
        }
    }

    private fun cleanupFiles(deprecatedOnly: Boolean) {
        val allFiles = setup.getAllLogFiles(true)
        val filesToDelete = if (deprecatedOnly) setup.filterFilesToDelete(allFiles) else allFiles
        filesToDelete.forEach {
            runCatching {
                SystemFileSystem.delete(it)
            }.onFailure { e ->
                platformPrintln(
                    "",
                    Level.ERROR,
                    TAG,
                    "Couldn't delete file: '${it}' (${e.message})"
                )
            }
        }
    }

    private fun createCloseBufferJob() {
        closeBufferJob = loggerScope.launch(Dispatchers.IO) {
            delay(timeToKeepFileOpen.milliseconds)
            if (isActive) {
                send(Event.CloseWriter)
            }
        }
    }

    private suspend fun cancelCloseBufferJob() {
        closeBufferJob?.cancelAndJoin()
        closeBufferJob = null
    }
/*
    private fun trimFileInMemory(path: Path, trimFactor: Float) {
        val content = SystemFileSystem
            .source(path)
            .buffered()
            .readString()
        val lines = content
            .lineSequence()
            .toList()
        val linesToKeep = (lines.size * trimFactor).toInt().coerceAtLeast(1)
        val trimmed = lines
            .takeLast(linesToKeep)
            .joinToString("\n")
        SystemFileSystem
            .sink(path, append = false)
            .buffered()
            .use { it.writeString(trimmed) }
    }
*/
    private fun trimFile(
        path: Path,
        trimFactor: Float,
    ) {
        require(trimFactor in 0f..1f)

        val size = SystemFileSystem
            .metadataOrNull(path)
            ?.size
            ?: return

        val startOffset = (size * (1f - trimFactor)).toLong()

        val tempPath = Path("$path.tmp")

        SystemFileSystem.source(path)
            .buffered()
            .use { source ->

                repeat(startOffset.toInt()) {
                    if (source.exhausted()) return@use
                    source.readByte()
                }

                while (!source.exhausted()) {
                    if (source.readByte().toInt().toChar() == '\n') {
                        break
                    }
                }

                SystemFileSystem.sink(tempPath, append = false)
                    .buffered()
                    .use { sink ->
                        val chunkSize = 8_192L
                        while (!source.exhausted()) {
                            try {
                                sink.write(source, chunkSize)
                            } catch (_: Exception) {
                                break
                            }
                        }
                    }
            }

        SystemFileSystem.atomicMove(
            tempPath,
            path
        )
    }


}