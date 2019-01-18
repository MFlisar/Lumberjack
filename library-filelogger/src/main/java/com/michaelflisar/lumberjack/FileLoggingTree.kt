package com.michaelflisar.lumberjack

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.*
import org.slf4j.LoggerFactory
import timber.log.BaseTree

/**
 * Created by Michael on 17.10.2016.
 */

class FileLoggingTree(setup: FileLoggingSetup?) : BaseTree() {

    private var mHandlerThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null

    init {

        if (setup == null) {
            throw RuntimeException("You can't create a FileLoggingTree without providing a setup!")
        }

        if (setup.logOnBackgroundThread) {
            mHandlerThread = HandlerThread("FileLoggingTree")
            mHandlerThread!!.start()
            mBackgroundHandler = Handler(mHandlerThread!!.looper)
        }

        init(setup)
    }

    private fun init(setup: FileLoggingSetup) {
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        lc.reset()

        // 1) FileLoggingSetup - Encoder for File
        val encoder1 = PatternLayoutEncoder()
        encoder1.context = lc
        encoder1.pattern = setup.logPattern
        encoder1.start()

        // 2) FileLoggingSetup - rolling file appender
        val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
        rollingFileAppender.isAppend = true
        rollingFileAppender.context = lc
        //rollingFileAppender.setFile(setup.folder + "/" + setup.fileName + "." + setup.fileExtension);

        // 3) FileLoggingSetup - Rolling policy (one log per day)
        var triggeringPolicy: TriggeringPolicy<ILoggingEvent>? = null
        when (setup.mode) {
            FileLoggingSetup.Mode.DateFiles -> {
                val timeBasedRollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
                timeBasedRollingPolicy.fileNamePattern = setup.folder + "/" + setup.fileName + "_%d{yyyyMMdd}." + setup.fileExtension
                timeBasedRollingPolicy.maxHistory = setup.logsToKeep
                timeBasedRollingPolicy.isCleanHistoryOnStart = true
                timeBasedRollingPolicy.setParent(rollingFileAppender)
                timeBasedRollingPolicy.context = lc

                triggeringPolicy = timeBasedRollingPolicy
            }
            FileLoggingSetup.Mode.NumberedFiles -> {
                val fixedWindowRollingPolicy = FixedWindowRollingPolicy()
                fixedWindowRollingPolicy.fileNamePattern = setup.folder + "/" + setup.fileName + "%i." + setup.fileExtension
                fixedWindowRollingPolicy.minIndex = 1
                fixedWindowRollingPolicy.maxIndex = setup.logsToKeep
                fixedWindowRollingPolicy.setParent(rollingFileAppender)
                fixedWindowRollingPolicy.context = lc

                val sizeBasedTriggeringPolicy = SizeBasedTriggeringPolicy<ILoggingEvent>()
                sizeBasedTriggeringPolicy.maxFileSize = setup.numberedFileSizeLimit

                triggeringPolicy = sizeBasedTriggeringPolicy

                rollingFileAppender.file = FileLoggingUtil.getDefaultLogFile(setup)
                rollingFileAppender.rollingPolicy = fixedWindowRollingPolicy
                fixedWindowRollingPolicy.start()
            }
        }
        triggeringPolicy.start()

        rollingFileAppender.setTriggeringPolicy(triggeringPolicy)
        rollingFileAppender.setEncoder(encoder1)
        rollingFileAppender.start()

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        val root = mLogger as ch.qos.logback.classic.Logger
        root.detachAndStopAllAppenders()
        root.addAppender(rollingFileAppender)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val logMessage = formatLine(tag, message)
        if (mBackgroundHandler == null) {
            doRealLog(priority, logMessage)
        } else {
            mBackgroundHandler!!.post { doRealLog(priority, logMessage) }
        }
    }

    private fun doRealLog(priority: Int, logMessage: String) {
        when (priority) {
            Log.VERBOSE -> mLogger.debug(logMessage)
            Log.DEBUG -> mLogger.debug(logMessage)
            Log.INFO -> mLogger.info(logMessage)
            Log.WARN -> mLogger.warn(logMessage)
            Log.ERROR -> mLogger.error(logMessage)
        }
    }

    companion object {

        val DATE_FILE_NAME_PATTERN = "%s_\\d{8}.%s"
        val NUMBERED_FILE_NAME_PATTERN = "%s\\d*.%s"

        internal var mLogger = LoggerFactory.getLogger(FileLoggingTree::class.java)//Logger.ROOT_LOGGER_NAME);
    }
}