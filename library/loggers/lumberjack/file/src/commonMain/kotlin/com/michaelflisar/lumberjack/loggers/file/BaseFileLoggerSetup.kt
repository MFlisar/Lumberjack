package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import dev.icerock.moko.parcelize.IgnoredOnParcel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

abstract class BaseFileLoggerSetup : FileLoggerSetup() {

    abstract val folder: String
    abstract val fileBaseName: String
    abstract val fileExtension: String

    @IgnoredOnParcel
    override val fileConverter = FileConverter

    @IgnoredOnParcel
    private var lastFileKey: String = ""

    @IgnoredOnParcel
    private var lastFileKeyChanged: Boolean = false

    override fun filePath(data: FileLogger.Event.Data): String {
        val lastPath = "${folder}/${fileBaseName}_${lastFileKey}.$fileExtension".toPath()
        val key = getFileKey(data, lastPath)
        val path = if (key.isEmpty()) {
            "${folder}/${fileBaseName}.$fileExtension"
        } else {
            "${folder}/${fileBaseName}_${key}.$fileExtension"
        }
        if (key != lastFileKey) {
            lastFileKey = key
            lastFileKeyChanged = true
        }
        return path
    }

    abstract fun getFileKey(data: FileLogger.Event.Data, lastPath: Path): String
    abstract fun filterLogFilesToDelete(files: List<Path>): List<Path>

    protected fun getKeyFromFile(file: Path): String {
        return file.name.dropLast(fileExtension.length + 1).replace(fileBaseName, "").substring(1)
    }

    override fun onLogged(scope: CoroutineScope) {
        if (lastFileKeyChanged) {
            lastFileKeyChanged = false
            scope.launch {
                clearLogFiles(false)
            }
        }
    }

    override suspend fun clearLogFiles() {
        clearLogFiles(true)
    }

    override fun getAllExistingLogFilePaths(): List<Path> {
        return if (FileSystem.SYSTEM.exists(folder.toPath())) {
             FileSystem.SYSTEM.list(folder.toPath()).filter {
                it.name.startsWith(fileBaseName) && it.name.endsWith(".$fileExtension")
            }.sortedByDescending { it.name }
        } else emptyList()
    }

    override fun getLatestLogFilePath(): Path? {
        return getAllExistingLogFilePaths().firstOrNull()
    }

    private suspend fun clearLogFiles(all: Boolean) {
        withContext(Dispatchers.IO) {
            val files = getAllExistingLogFilePaths()
            val filesToDelete = if (all) files else filterLogFilesToDelete(files)
            if (filesToDelete.isNotEmpty()) {
                LumberjackLogger.loggers()
                    .filterIsInstance<FileLogger>()
                    .filter {
                        it.setup == this@BaseFileLoggerSetup
                    }
                    .forEach {
                        it.onLogFilesWillBeDeleted(filesToDelete)
                    }
                filesToDelete.forEach {
                    FileSystem.SYSTEM.delete(it)
                }
            }
        }
    }
}