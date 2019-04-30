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
 * A {@link timber.log.Timber.Tree} implementation that performs logging to a file.
 *
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingTree extends BaseTree {
    private Handler mBackgroundHandler = null;

    private static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);

    public FileLoggingTree(boolean combineTags, FileLoggingSetup setup, ILogFilter filter) {
        super(combineTags, false, filter);

        if (setup.isLogOnBackgroundThread()) {
            HandlerThread mHandlerThread = new HandlerThread("lumberjack.FileLoggingTree");
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
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(lc);

        // 3) FileLoggingSetup - Rolling policies
        TriggeringPolicy<ILoggingEvent> triggeringPolicy = null;
        switch (setup.getFileLoggingMode()) {
            case DAILY_ROLLOVER:
            case WEEKLY_ROLLOVER://both time based policy cases are taken care by their file naming pattern
                TimeBasedRollingPolicy<ILoggingEvent> timeBasedRollingPolicy = new TimeBasedRollingPolicy<>();
                timeBasedRollingPolicy.setFileNamePattern(setup.getFolderPath() + "/"
                        + setup.getFileName() + setup.getFileLoggingMode().getFileNamePattern()
                        + "." + setup.getFileExtension()
                );
                timeBasedRollingPolicy.setMaxHistory(setup.getLogFilesToKeep());
                timeBasedRollingPolicy.setCleanHistoryOnStart(true);
                timeBasedRollingPolicy.setParent(rollingFileAppender);
                timeBasedRollingPolicy.setContext(lc);
                timeBasedRollingPolicy.setTotalSizeCap(FileSize.valueOf(setup.getFileSizeLimit()));
                triggeringPolicy = timeBasedRollingPolicy;
                break;

            case MONTHLY_ROLLOVER:
                TimeBasedRollingPolicy<ILoggingEvent> monthlyRollingPolicy = new TimeBasedRollingPolicy<>();
                monthlyRollingPolicy.setFileNamePattern(setup.getFolderPath() + "/"
                        + setup.getFileLoggingMode().getFileNamePattern()
                        + setup.getFileName() + "." + setup.getFileExtension()
                );
                monthlyRollingPolicy.setMaxHistory(setup.getLogFilesToKeep());
                monthlyRollingPolicy.setCleanHistoryOnStart(true);
                monthlyRollingPolicy.setParent(rollingFileAppender);
                monthlyRollingPolicy.setContext(lc);
                monthlyRollingPolicy.setTotalSizeCap(FileSize.valueOf(setup.getFileSizeLimit()));
                triggeringPolicy = monthlyRollingPolicy;
                break;

            case FILE_SIZE_ROLLOVER:
                FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
                fixedWindowRollingPolicy.setFileNamePattern(setup.getFolderPath() + "/"
                        + setup.getFileName() + setup.getFileLoggingMode().getFileNamePattern()
                        + "." + setup.getFileExtension()
                );
                fixedWindowRollingPolicy.setMinIndex(1);
                fixedWindowRollingPolicy.setMaxIndex(setup.getLogFilesToKeep());
                fixedWindowRollingPolicy.setParent(rollingFileAppender);
                fixedWindowRollingPolicy.setContext(lc);

                SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
                sizeBasedTriggeringPolicy.setMaxFileSize(FileSize.valueOf(setup.getFileSizeLimit()));

                triggeringPolicy = sizeBasedTriggeringPolicy;

                rollingFileAppender.setFile(setup.getFolderPath() + "/" + setup.getFileName() + "." + setup.getFileExtension());
                rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
                fixedWindowRollingPolicy.start();
                break;

            default:
                throw new IllegalStateException("UnImplemented case: " + setup.getFileLoggingMode());
        }
        triggeringPolicy.start();

        rollingFileAppender.setTriggeringPolicy(triggeringPolicy);
        rollingFileAppender.setEncoder(encoder1);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger
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
            default:
                mLogger.debug(logMessage);
        }
    }
}