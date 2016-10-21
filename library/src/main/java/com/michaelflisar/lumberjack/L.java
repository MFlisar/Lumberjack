package com.michaelflisar.lumberjack;

import android.util.Log;

import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;
import com.michaelflisar.lumberjack.formatter.ILogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import timber.log.Timber;

/**
 * Created by flisar on 20.10.2016.
 */

/*
* Main Logger
*
* Forwards all calls directly to timber
*
* Offers a shorter interface (L instead of Timber) for a more clean and shorter code
* Additionally, this offers the ability to print a list of values and also convenient methods to log arrays
 */
public class L
{
    // --------------------
    // Setup
    // --------------------

    private static ILogFormatter mLogFormatter = null;

    public static ILogFormatter getFormatter()
    {
        // if the user has not provided a custom formatter, we create the default one!
        if (mLogFormatter == null)
            throw new RuntimeException("You did not initialise the logger's formatter!");
        return mLogFormatter;
    }

    public static void setLogFormatter(ILogFormatter formatter)
    {
        mLogFormatter = formatter;
    }

    // --------------------
    // Logging - Debug
    // --------------------

    public static void d(String message, Object... args)
    {
        log(null, Log.DEBUG, message, args);
    }

    public static void d(Throwable t, String message, Object... args)
    {
        log(null, Log.DEBUG, t, message, args);
    }

    public static void d(Throwable t)
    {
        log(null, Log.DEBUG, t);
    }

    public static void d(ILogGroup group, String message, Object... args)
    {
        log(group, Log.DEBUG, message, args);
    }

    public static void d(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.DEBUG, t, message, args);
    }

    public static void d(ILogGroup group, Throwable t)
    {
        log(group, Log.DEBUG, t);
    }

    public static void dLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.DEBUG, args);
    }

    public static void dLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.DEBUG, args);
    }

    // --------------------
    // Logging - Debug
    // --------------------

    public static void e(String message, Object... args)
    {
        log(null, Log.ERROR, message, args);
    }

    public static void e(Throwable t, String message, Object... args)
    {
        log(null, Log.ERROR, t, message, args);
    }

    public static void e(Throwable t)
    {
        log(null, Log.ERROR, t);
    }

    public static void e(ILogGroup group, String message, Object... args)
    {
        log(group, Log.ERROR, message, args);
    }

    public static void e(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.ERROR, t, message, args);
    }

    public static void e(ILogGroup group, Throwable t)
    {
        log(group, Log.ERROR, t);
    }

    public static void eLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.ERROR, args);
    }

    public static void eLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.ERROR, args);
    }

    // --------------------
    // Main debug functions
    // --------------------

    private static String ERROR_LOG_VALUES = "This function needs arguments in the format of \"String label, Object value, String label, Object value, ...\" and for each label it needs a value!";

    private static void logLabeledValuePairs(ILogGroup group, int priority, Object... args)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) prepare log string
        if (args.length % 2 != 0)
            throw new RuntimeException(ERROR_LOG_VALUES);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i += 2)
        {
            if (!(args[i] instanceof String))
                throw new RuntimeException(ERROR_LOG_VALUES);

            sb.append(args[i]).append("=");
            if (mFormatters.size() == 0)
                sb.append(args[i + 1]);
            else
                sb.append(formatArg(args[i + 1]));
        }

        // 3) set tag
        updateTag(group);

        // 4) log
        Timber.log(priority, "%s", sb.toString());
    }

    private static void log(ILogGroup group, int priority, String message, Object... args)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) log
        Timber.log(priority, message, formatArgs(args));
    }

    private static void log(ILogGroup group, int priority, Throwable t, String message, Object... args)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) log
        Timber.log(priority, t, message, formatArgs(args));
    }

    private static void log(ILogGroup group, int priority, Throwable t)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) log
        Timber.log(priority, t);
    }

    // --------------------
    // Formatter
    // --------------------

    private static void updateTag(ILogGroup group)
    {
        if (group == null)
            Timber.tag(null);
        else
            Timber.tag(group.getTag());
    }

    private static Object formatArg(Object arg)
    {
        Object formatterArg;
        if (arg instanceof Collection)
            formatterArg = getFormatter().format((Collection) arg, mFormatters);
        else
            formatterArg = getFormatter().format(arg, mFormatters, false);

        return formatterArg;
    }


    private static Object[] formatArgs(Object[] args)
    {
        if (args.length == 0)
            return args;

        Object[] formatterArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            formatterArgs[i] = formatArg(args[i]);

        return formatterArgs;
    }

    private static HashMap<Class, ILogClassFormatter> mFormatters = new HashMap<>();

    public static <T> void registerFormatter(Class<T> clazz, ILogClassFormatter<T> formatter)
    {
        mFormatters.put(clazz, formatter);
    }

    // --------------------
    // Groups
    // --------------------

    private static HashSet<ILogGroup> mDisabledGroups = new HashSet<>();

    public static void disableLogGroup(ILogGroup group)
    {
        mDisabledGroups.add(group);
    }

    public static void enableLogGroup(ILogGroup group)
    {
        mDisabledGroups.remove(group);
    }

    public static void enableAllLogGroup()
    {
        mDisabledGroups.clear();
    }

    private static boolean isGroupEnabled(ILogGroup group)
    {
        if (group == null || mDisabledGroups.size() == 0)
            return true;
        else
            return !mDisabledGroups.contains(group);
    }
}