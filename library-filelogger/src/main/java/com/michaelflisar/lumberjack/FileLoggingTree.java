package com.michaelflisar.lumberjack;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.michaelflisar.lumberjack.filter.ILogFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import timber.log.BaseTree;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingTree extends BaseTree {
    public static final String DATE_FILE_NAME_PATTERN = "%s_\\d{8}.%s";
    public static final String NUMBERED_FILE_NAME_PATTERN = "%s\\d*.%s";

    private HandlerThread mHandlerThread = null;
    private Handler mBackgroundHandler = null;

    static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);//Logger.ROOT_LOGGER_NAME);

    public FileLoggingTree(boolean combineTags, FileLoggingSetup setup, ILogFilter filter) {
        super(combineTags, false, filter);

        if (setup == null) {
            throw new RuntimeException("You can't create a FileLoggingTree without providing a setup!");
        }

        if (setup.isLogOnBackgroundThread()) {
            mHandlerThread = new HandlerThread("FileLoggingTree");
            mHandlerThread.start();
            mBackgroundHandler = new Handler(mHandlerThread.getLooper());
        }

        init(setup);
    }

    private void init(FileLoggingSetup setup) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        // 1) FileLoggingSetup - Encoder for File
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern(setup.getLogPattern());
        encoder1.start();

        // 2) FileLoggingSetup - rolling file appender
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(lc);
        //rollingFileAppender.setFile(setup.mFolder + "/" + setup.mFileName + "." + setup.mFileExtension);

        // 3) FileLoggingSetup - Rolling policy (one log per day)
        TriggeringPolicy<ILoggingEvent> triggeringPolicy = null;
        switch (setup.getFileLoggingMode()) {
            case DATE_FILES: {
                TimeBasedRollingPolicy timeBasedRollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
                timeBasedRollingPolicy.setFileNamePattern(setup.getFolder() + "/" + setup.getFileName() + "_%d{yyyyMMdd}." + setup.getFileExtension());
                timeBasedRollingPolicy.setMaxHistory(setup.getLogsToKeep());
                timeBasedRollingPolicy.setCleanHistoryOnStart(true);
                timeBasedRollingPolicy.setParent(rollingFileAppender);
                timeBasedRollingPolicy.setContext(lc);

                triggeringPolicy = timeBasedRollingPolicy;
                break;
            }
            case NUMBERED_FILES: {
                FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
                fixedWindowRollingPolicy.setFileNamePattern(setup.getFolder() + "/" + setup.getFileName() + "%i." + setup.getFileExtension());
                fixedWindowRollingPolicy.setMinIndex(1);
                fixedWindowRollingPolicy.setMaxIndex(setup.getLogsToKeep());
                fixedWindowRollingPolicy.setParent(rollingFileAppender);
                fixedWindowRollingPolicy.setContext(lc);

                SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
                sizeBasedTriggeringPolicy.setMaxFileSize(FileSize.valueOf(setup.getNumberedFileSizeLimit()));

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
    protected void doLog(final int priority, String tag, String message, Throwable t) {
        final String logMessage = L.getFormatter().formatLine(tag, message);
        if (mBackgroundHandler == null) {
            doRealLog(priority, logMessage);
        } else {
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    doRealLog(priority, logMessage);
                }
            });
        }
    }

    private void doRealLog(int priority, String logMessage) {
        switch (priority) {
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