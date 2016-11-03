package com.michaelflisar.lumberjack;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import timber.log.DebugTree;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingTree extends DebugTree
{
    public static final String FILE_NAME_PATTERN = "%s_\\d{8}.%s";

    static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);//Logger.ROOT_LOGGER_NAME);

    public FileLoggingTree(boolean combineTags, FileLoggingSetup setup)
    {
        super(combineTags);

        if (setup == null)
            throw new RuntimeException("You can't create a FileLiggingTree without providing a setup!");

        init(setup);
    }

    private void init(FileLoggingSetup setup)
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        // 1) FileLoggingSetup - Encoder for File
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern(setup.mLogPattern);
        encoder1.start();

        // 2) FileLoggingSetup - rolling file appender
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(lc);
        //rollingFileAppender.setFile(setup.mFolder + "/" + setup.mFileName + "." + setup.mFileExtension);

        // 3) FileLoggingSetup - Rolling policy (one log per day)
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern(setup.mFolder + "/" + setup.mFileName + "_%d{yyyyMMdd}." + setup.mFileExtension);
        rollingPolicy.setMaxHistory(setup.mDaysToKeep);
        rollingPolicy.setCleanHistoryOnStart(true);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(lc);
        rollingPolicy.start();

        rollingFileAppender.setTriggeringPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder1);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) mLogger;
        root.detachAndStopAllAppenders();
        root.addAppender(rollingFileAppender);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t)
    {
        String logMessage = L.getFormatter().formatLine(tag, message);
        switch (priority)
        {
            case Log.VERBOSE:
                mLogger.debug(logMessage);
                break;
            case Log.DEBUG:
                mLogger.debug(logMessage);
                break;
            case Log.INFO:
                mLogger.info(logMessage);
                break;
            case Log.WARN:
                mLogger.warn(logMessage);
                break;
            case Log.ERROR:
                mLogger.error(logMessage);
                break;
        }
    }
}