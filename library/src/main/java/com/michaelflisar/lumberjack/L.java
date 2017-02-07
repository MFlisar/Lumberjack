package com.michaelflisar.lumberjack;

import android.util.Log;

import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;
import com.michaelflisar.lumberjack.formatter.ILogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogGroup;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import timber.log.Lumberjack;
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
* And it allows to adjust the call stack depth used to retrieve the callers class name
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
    // Logging - VERBOSE
    // --------------------

    public static void v(String message, Object... args)
    {
        log(null, Log.VERBOSE, null, message, args);
    }

    public static void v(Throwable t, String message, Object... args)
    {
        log(null, Log.VERBOSE, null, t, message, args);
    }

    public static void v(Throwable t)
    {
        log(null, Log.VERBOSE, null, t);
    }

    public static void v(ILogGroup group, String message, Object... args)
    {
        log(group, Log.VERBOSE, null, message, args);
    }

    public static void v(int callStackCorrection, String message, Object... args)
    {
        log(null, Log.VERBOSE, callStackCorrection, message, args);
    }

    public static void v(ILogGroup group, int callStackCorrection, String message, Object... args)
    {
        log(group, Log.VERBOSE, callStackCorrection, message, args);
    }

    public static void v(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.VERBOSE, null, t, message, args);
    }

    public static void v(ILogGroup group, Throwable t)
    {
        log(group, Log.VERBOSE, null, t);
    }

    public static void vLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.VERBOSE, null, args);
    }

    public static void vLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.VERBOSE, null, args);
    }

    // --------------------
    // Logging - DEBUG
    // --------------------

    public static void d(String message, Object... args)
    {
        log(null, Log.DEBUG, null, message, args);
    }

    public static void d(Throwable t, String message, Object... args)
    {
        log(null, Log.DEBUG, null, t, message, args);
    }

    public static void d(Throwable t)
    {
        log(null, Log.DEBUG, null, t);
    }

    public static void d(ILogGroup group, String message, Object... args)
    {
        log(group, Log.DEBUG, null, message, args);
    }

    public static void d(int callStackCorrection, String message, Object... args)
    {
        log(null, Log.DEBUG, callStackCorrection, message, args);
    }

    public static void d(ILogGroup group, int callStackCorrection, String message, Object... args)
    {
        log(group, Log.DEBUG, callStackCorrection, message, args);
    }

    public static void d(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.DEBUG, null, t, message, args);
    }

    public static void d(ILogGroup group, Throwable t)
    {
        log(group, Log.DEBUG, null, t);
    }

    public static void dLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.DEBUG, null, args);
    }

    public static void dLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.DEBUG, null, args);
    }

    // --------------------
    // Logging - INFO
    // --------------------

    public static void i(String message, Object... args)
    {
        log(null, Log.INFO, null, message, args);
    }

    public static void i(Throwable t, String message, Object... args)
    {
        log(null, Log.INFO, null, t, message, args);
    }

    public static void i(Throwable t)
    {
        log(null, Log.INFO, null, t);
    }

    public static void i(ILogGroup group, String message, Object... args)
    {
        log(group, Log.INFO, null, message, args);
    }

    public static void i(int callStackCorrection, String message, Object... args)
    {
        log(null, Log.INFO, callStackCorrection, message, args);
    }

    public static void i(ILogGroup group, int callStackCorrection, String message, Object... args)
    {
        log(group, Log.INFO, callStackCorrection, message, args);
    }

    public static void i(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.INFO, null, t, message, args);
    }

    public static void i(ILogGroup group, Throwable t)
    {
        log(group, Log.INFO, null, t);
    }

    public static void iLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.INFO, null, args);
    }

    public static void iLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.INFO, null, args);
    }

    // --------------------
    // Logging - WARN
    // --------------------

    public static void w(String message, Object... args)
    {
        log(null, Log.WARN, null, message, args);
    }

    public static void w(Throwable t, String message, Object... args)
    {
        log(null, Log.WARN, null, t, message, args);
    }

    public static void w(Throwable t)
    {
        log(null, Log.WARN, null, t);
    }

    public static void w(ILogGroup group, String message, Object... args)
    {
        log(group, Log.WARN, null, message, args);
    }

    public static void w(int callStackCorrection, String message, Object... args)
    {
        log(null, Log.WARN, callStackCorrection, message, args);
    }

    public static void w(ILogGroup group, int callStackCorrection, String message, Object... args)
    {
        log(group, Log.WARN, callStackCorrection, message, args);
    }

    public static void w(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.WARN, null, t, message, args);
    }

    public static void w(ILogGroup group, Throwable t)
    {
        log(group, Log.WARN, null, t);
    }

    public static void wLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.WARN, null, args);
    }

    public static void wLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.WARN, null, args);
    }

    // --------------------
    // Logging - Error
    // --------------------

    public static void e(String message, Object... args)
    {
        log(null, Log.ERROR, null, message, args);
    }

    public static void e(Throwable t, String message, Object... args)
    {
        log(null, Log.ERROR, null, t, message, args);
    }

    public static void e(Throwable t)
    {
        log(null, Log.ERROR, null, t);
    }

    public static void e(ILogGroup group, String message, Object... args)
    {
        log(group, Log.ERROR, null, message, args);
    }

    public static void e(int callStackCorrection, String message, Object... args)
    {
        log(null, Log.ERROR, callStackCorrection, message, args);
    }

    public static void e(ILogGroup group, int callStackCorrection, String message, Object... args)
    {
        log(group, Log.ERROR, callStackCorrection, message, args);
    }

    public static void e(ILogGroup group, Throwable t, String message, Object... args)
    {
        log(group, Log.ERROR, null, t, message, args);
    }

    public static void e(ILogGroup group, Throwable t)
    {
        log(group, Log.ERROR, null, t);
    }

    public static void eLabeledValuePairs(Object... args)
    {
        logLabeledValuePairs(null, Log.ERROR, null, args);
    }

    public static void eLabeledValuePairs(ILogGroup group, Object... args)
    {
        logLabeledValuePairs(group, Log.ERROR, null, args);
    }

    // --------------------
    // Main debug functions
    // --------------------

    private static String ERROR_LOG_VALUES = "This function needs arguments in the format of \"String label, Object value, String label, Object value, ...\" and for each label it needs a value!";

    private static void logLabeledValuePairs(ILogGroup group, int priority, Integer callStackCorrection, Object... args)
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

            if (i > 0)
                sb.append(", ");
            sb.append(args[i]).append("=");
            if (mFormatters.size() == 0)
                sb.append(args[i + 1]);
            else
                sb.append(formatArg(args[i + 1]));
        }

        // 3) set tag
        updateTag(group);

        // 4) set call stack correction
        updateStackDepth(callStackCorrection);

        // 5) log
        Timber.log(priority, "%s", sb.toString());
    }

    public static void log(ILogGroup group, int priority, Integer callStackCorrection, String message, Object... args)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) set call stack correction
        updateStackDepth(callStackCorrection);

        // 4) log
        Timber.log(priority, message, formatArgs(args));
    }

    public static void log(ILogGroup group, int priority, Integer callStackCorrection, Throwable t, String message, Object... args)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) set call stack correction
        updateStackDepth(callStackCorrection);

        //4 ) log
        Timber.log(priority, t, message, formatArgs(args));
    }

    public static void log(ILogGroup group, int priority, Integer callStackCorrection, Throwable t)
    {
        // 1) check if group is enabled
        if (!isGroupEnabled(group))
            return;

        // 2) set tag
        updateTag(group);

        // 3) set call stack correction
        updateStackDepth(callStackCorrection);

        // 4) log
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

    private static void updateStackDepth(Integer correction)
    {
        if (correction == null)
            Lumberjack.callStackCorrection(null);
        else
            Lumberjack.callStackCorrection(correction);
    }

    private static Object formatArg(Object arg)
    {
        Object formatterArg;
        if (arg instanceof Collection)
            formatterArg = getFormatter().format((Collection) arg, mFormatters);
        else if (arg instanceof Object[])
            formatterArg = getFormatter().format((Object[]) arg, mFormatters);
        // check primitive types one by one to avoid reflection in this check function, which will be checked for all objects!
//        else if(arg != null && arg.getClass().isArray())
        else if (arg instanceof boolean[] || arg instanceof byte[] || arg instanceof short[] || arg instanceof char[] ||
                arg instanceof int[] || arg instanceof long[] || arg instanceof float[] || arg instanceof double[])
        {
            Object[] objArgs = new Object[Array.getLength(arg)];
            for(int i = 0; i < Array.getLength(arg) ; i++)
                objArgs[i] = Array.get(arg, i);
            formatterArg = getFormatter().format(objArgs, mFormatters);
        }
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

    public static ILogGroup createGroup(final String tag)
    {
        return new ILogGroup() {
            @Override
            public String getTag() {
                return tag;
            }
        };
    }
}