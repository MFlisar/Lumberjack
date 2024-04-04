package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.IgnoredOnParcel
import java.io.File

abstract class BaseFileLoggerSetup : FileLoggerSetup() {

    abstract val folder: File
    abstract val fileBaseName: String
    abstract val fileExtension: String
    abstract var lastFileKey: String
    abstract var lastFileKeyChanged: Boolean

    @IgnoredOnParcel
    override val fileConverter = FileConverter

    override fun filePath(data: FileLogger.Event.Data): String {
        val lastPath = "${folder.path}/${fileBaseName}_${lastFileKey}.$fileExtension"
        val key = getFileKey(data, lastPath)
        val path = "${folder.path}/${fileBaseName}_${key}.$fileExtension"
        if (key != lastFileKey) {
            lastFileKey = key
            lastFileKeyChanged = true
        }
        return path
    }

    abstract fun getFileKey(data: FileLogger.Event.Data, lastPath: String): String
    abstract fun filterLogFilesToDelete(files: List<File>): List<File>

    protected fun getKeyFromFile(file: File) : String {
        return file.nameWithoutExtension.replace(fileBaseName, "").substring(1)
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

    override fun getAllExistingLogFiles(): List<File> {
        return folder.listFiles()?.filter {
            it.name.startsWith(fileBaseName)
        }?.sortedByDescending { it.name } ?: emptyList()
    }

    override fun getLatestLogFiles(): File? {
        return getAllExistingLogFiles().firstOrNull()
    }

    private suspend fun clearLogFiles(all: Boolean) {
        withContext(Dispatchers.IO) {
            val files = getAllExistingLogFiles()
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
                    it.delete()
                }
            }
        }
    }
}