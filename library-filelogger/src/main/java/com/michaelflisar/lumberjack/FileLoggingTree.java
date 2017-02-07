package com.michaelflisar.lumberjack;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import timber.log.BaseTree;
import timber.log.Lumberjack;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingTree extends BaseTree
{
    public static final String DATE_FILE_NAME_PATTERN = "%s_\\d{8}.%s";
    public static final String NUMBERED_FILE_NAME_PATTERN = "%s\\d*.%s";

    static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);//Logger.ROOT_LOGGER_NAME);

    public FileLoggingTree(boolean combineTags, FileLoggingSetup setup)
    {
        super(combineTags, false);

        if (setup == null)
            throw new RuntimeException("You can't create a FileLoggingTree without providing a setup!");

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
        TriggeringPolicy<ILoggingEvent> triggeringPolicy = null;
        switch (setup.mMode)
        {
            case DateFiles:
            {
                TimeBasedRollingPolicy timeBasedRollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
                timeBasedRollingPolicy.setFileNamePattern(setup.mFolder + "/" + setup.mFileName + "_%d{yyyyMMdd}." + setup.mFileExtension);
                timeBasedRollingPolicy.setMaxHistory(setup.mLogsToKeep);
                timeBasedRollingPolicy.setCleanHistoryOnStart(true);
                timeBasedRollingPolicy.setParent(rollingFileAppender);
                timeBasedRollingPolicy.setContext(lc);

                triggeringPolicy = timeBasedRollingPolicy;
                break;
            }
            case NumberedFiles:
            {
                FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
                fixedWindowRollingPolicy.setFileNamePattern(setup.mFolder + "/" + setup.mFileName + "%i." + setup.mFileExtension);
                fixedWindowRollingPolicy.setMinIndex(1);
                fixedWindowRollingPolicy.setMaxIndex(setup.mLogsToKeep);
                fixedWindowRollingPolicy.setParent(rollingFileAppender);
                fixedWindowRollingPolicy.setContext(lc);

                SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
                sizeBasedTriggeringPolicy.setMaxFileSize(setup.mNumberedFileSizeLimit);

                triggeringPolicy = sizeBasedTriggeringPolicy;

                rollingFileAppender.setFile(FileLoggingUtil.getDefaultLogFile(setup));
                rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
                fixedWindowRollingPolicy.start();

                break;
            }
        }
        triggeringPolicy.start();

        rollingFileAppender.setTriggeringPolicy(triggeringPolicy);
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