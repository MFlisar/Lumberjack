package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.parcelize.IgnoredOnParcel
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

abstract class BaseFileLoggerSetup : IFileLoggingSetup {

    sealed class RollOverType {

        data object None : RollOverType()

        data class TrimFile(
            val trimFactor: Float
        ) : RollOverType()

        data object SwitchFile : RollOverType()

        data class ArchiveFile(
            val archivePath: Path
        ) : RollOverType()
    }

    abstract val folder: String
    abstract val fileBaseName: String
    abstract val fileExtension: String

    @IgnoredOnParcel
    override val fileConverter = FileConverter

    abstract fun getActiveLogFilePath(event: FileLogger.Event.Log): Path

    abstract fun getAllLogFiles(sorted: Boolean): List<Path>

    abstract fun getRollOverType(currentPath: Path?, event: FileLogger.Event.Log): RollOverType

    abstract fun filterFilesToDelete(
        files: List<Path>
    ): List<Path>

    // -----------------
    // IFileLoggingSetup
    // -----------------

    override fun getAllExistingLogFilePaths(): List<Path> {
        return getAllLogFiles(true).filter { SystemFileSystem.exists(it) }
    }

    override fun getLatestLogFilePath(): Path? {
        return getAllExistingLogFilePaths().firstOrNull()
    }

    override suspend fun clearLogFiles() {
        LumberjackLogger.loggers()
            .filterIsInstance<FileLogger>()
            .firstOrNull { it.setup == this }
            ?.cleanAllLogFiles()
    }
}