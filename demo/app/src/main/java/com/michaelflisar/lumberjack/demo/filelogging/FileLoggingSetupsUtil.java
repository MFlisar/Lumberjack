package com.michaelflisar.lumberjack.demo.filelogging;

import android.content.Context;
import android.support.annotation.NonNull;

import com.michaelflisar.lumberjack.FileLoggingMode;
import com.michaelflisar.lumberjack.FileLoggingSetup;

/**
 * A utility to show all variants for FileLoggingSetup
 */
public final class FileLoggingSetupsUtil {
    private FileLoggingSetupsUtil() {
    }


    /**
     * This method will return a FileLoggingSetup which:
     * <ol>
     *     <li>Stores the logs in ~/daily_logs/ folder (~ apps default files directory)</li>
     *     <li>Generates a new log file on daily basis</li>
     *     <li>The maximum number of log files will be 5(i.e., 5 days)</li>
     *     <li>The maximum size of all log files combined will be 10Mb</li>
     *     <li>The logging will happen on a background thread</li>
     * </ol>
     * <br/>
     * The name of the file will be of the pattern `fooyyyy-MM-dd.log`.
     * <br/>
     * <b>Note:</b> If there are 3 days of logs and the total file size limit is reached, the oldest
     * log file(i.e., of day 1) will be deleted. Similarly if there is only 1day of logs and the
     * file size exceeds the limit, the whole file will be cleared and start afresh. Hence, please
     *      * fix the fileSizeLimit according to your apps usage.
     *
     * @param context the Context
     * @return the FileLoggingSetup with the above features
     */
    public static FileLoggingSetup getDailyLoggingSetup(@NonNull final Context context) {
        return new FileLoggingSetup.Builder(context.getApplicationContext())
                .setFileLoggingMode(FileLoggingMode.DAILY_ROLLOVER) //use new log daily
                .setFolderPath(context.getFileStreamPath("").getAbsolutePath() + "/daily_logs")
                .setFileName("foo")         //set the name of the file without extension
                .setFileExtension("log")    //set just the extension
                .setLogFilesToKeep(5)       //store max 5 log files(i.e., 5 days)
                .setFileSizeLimit("10MB")   //sets the max file size limit(here, for 5 log files)
                .setLogOnBackgroundThread(true) //use a background thread for logging to file
                .build();   //build the setup
    }

    /**
     * This method will return a FileLoggingSetup which:
     * <ol>
     *     <li>Stores the logs in ~/weekly_logs/ folder (~ apps default files directory)</li>
     *     <li>Generates a new log file on weekly basis</li>
     *     <li>The maximum number of log files will be 3(i.e., 3 weeks)</li>
     *     <li>The maximum size of all log files combined will be 400Mb</li>
     *     <li>Since the extension is `zip`, the library takes care of compressing the file </li>
     *     <li>The logging will happen on a background thread</li>
     * </ol>
     * <br/>
     * The name of the file will be of the pattern `fooyyyy-ww.zip` for archives and `fooyyy-ww` for current.
     * <br/>
     * <b>Note:</b> If there are 2 weeks of logs and the total file size limit is reached, the oldest
     * log file(i.e., of week 1) will be deleted. Similarly if there is only 1week of logs and the
     * file size exceeds the limit, the whole file will be cleared and start afresh. Hence, please
     * fix the fileSizeLimit according to your apps usage.
     *
     * @param context the Context
     * @return the FileLoggingSetup with the above features
     */
    public static FileLoggingSetup getWeeklyLoggingSetup(@NonNull final Context context) {
        return new FileLoggingSetup.Builder(context)
                .setFileLoggingMode(FileLoggingMode.WEEKLY_ROLLOVER)    //use new log weekly
                .setFolderPath(context.getFileStreamPath("").getAbsolutePath() + "/weekly_logs")
                .setFileName("foo")         //set the name of the file without extension
                .setFileExtension("zip")    //set just the extension; we take care of compressing the file
                .setLogFilesToKeep(3)       //store max 3 log files(i.e., 3 weeks)
                .setFileSizeLimit("400MB")   //sets the max file size limit(here, for 3 log files)
                .setLogOnBackgroundThread(true) //use a background thread for logging to file
                .build();   //build the setup
    }

    /**
     * This method will return a FileLoggingSetup which:
     * <ol>
     *     <li>Stores the logs in ~/monthly_logs/ folder (~ apps default files directory)</li>
     *     <li>Generates a new log file on monthly basis</li>
     *     <li>The maximum number of log files will be 2(i.e., 2 months)</li>
     *     <li>The maximum size of all log files combined will be 800Mb</li>
     *     <li>Since the extension is `gz`, the library takes care of compressing the file </li>
     *     <li>The logging will happen on UI thread</li>
     * </ol>
     * <br/>
     * The name of the file will be of the pattern `yyyy/mm/foo.gz` for archives and `yyyy/mm/foo` for current.
     * <br/>
     * <b>Note:</b> If there are 2 months of logs and the total file size limit is reached, the log
     * file of previous month will be deleted. Similarly if there is only 1month of logs and the
     * file size exceeds the limit, the whole file will be cleared and start afresh. Hence, please
     * fix the fileSizeLimit according to your apps usage.
     *
     * @param context the Context
     * @return the FileLoggingSetup with the above features
     */
    public static FileLoggingSetup getMonthlyLoggingSetup(@NonNull final Context context) {
        return new FileLoggingSetup.Builder(context)
                .setFileLoggingMode(FileLoggingMode.MONTHLY_ROLLOVER)   //use new log monthly
                .setFolderPath(context.getFileStreamPath("").getAbsolutePath() + "/monthly_logs")
                .setFileName("foo")         //set the name of the file without extension
                .setFileExtension("gz")     //set just the extension; we take care of compressing the file
                .setLogFilesToKeep(2)       //store max 2 log files(i.e., 2 months)
                .setFileSizeLimit("800MB")   //sets the max file size limit(here, for 2 log files)
                .setLogOnBackgroundThread(false) //use UI thread for logging to file
                .build();   //build the setup
    }

    /**
     * This method will return a FileLoggingSetup which:
     * <ol>
     *     <li>Stores the logs in ~/file_logs/ folder (~ apps default files directory)</li>
     *     <li>Generates a new log file once the file size is reached</li>
     *     <li>The maximum number of log files will be 5</li>
     *     <li>The maximum size of all log files combined will be 5 * 10Mb = 50Mb</li>
     *     <li>Since the extension is `zip`, the library takes care of compressing the file </li>
     *     <li>The logging will happen on a background thread</li>
     * </ol>
     * <br/>
     * The name of the file will be of the pattern `foo%d.zip`
     * <br/>
     * <b>Note:</b> If there are 5 log files and the total file size limit for each is reached, the
     * 1st log file will be deleted.
     *
     * @param context the Context
     * @return the FileLoggingSetup with the above features
     */
    public static FileLoggingSetup getFileSizedLoggingSetup(@NonNull final Context context) {
        return new FileLoggingSetup.Builder(context)
                .setFileLoggingMode(FileLoggingMode.FILE_SIZE_ROLLOVER) //use new log once file size is met
                .setFolderPath(context.getFileStreamPath("").getAbsolutePath() + "/file_logs")
                .setFileName("foo")         //set the name of the file without extension
                .setFileExtension("zip")    //set just the extension
                .setLogFilesToKeep(5)       //store max 5 log files
                .setFileSizeLimit("10MB")   //sets the size of each log file(here, 10Mb for each file)
                .setLogOnBackgroundThread(true) //use a background thread for logging to file
                .build();   //build the setup
    }


}
