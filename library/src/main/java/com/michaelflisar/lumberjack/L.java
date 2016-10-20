package com.michaelflisar.lumberjack;

import com.michaelflisar.lumberjack.formatter.DefaultLogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;

import java.util.Collection;
import java.util.HashMap;

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
    // Debug - DEFAULT
    // --------------------

    public static void d(String message, Object... args)
    {
        Timber.d(message, formatArgs(args));
    }

    public static void d(Throwable t, String message, Object... args)
    {
        Timber.d(t, message, formatArgs(args));
    }

    public static void d(Throwable t)
    {
        Timber.d(t);
    }

    // --------------------
    // Debug - EXTRA
    // --------------------

    private static String ERROR_LOG_VALUES = "This function needs arguments in the format of \"String label, Object value, String label, Object value, ...\" and for each label it needs a value!";

    public static void dLabeledValuePairs(Object... args)
    {
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
        Timber.d("%s", sb.toString());
    }

    // --------------------
    // Formatter
    // --------------------

    private static String formatArg(Object arg)
    {
        String formatterArg;
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

        String[] formatterArgs = new String[args.length];
        for (int i = 0; i < args.length; i++)
            formatterArgs[i] = formatArg(args[i]);

        return formatterArgs;
    }

    private static HashMap<Class, ILogClassFormatter> mFormatters = new HashMap<>();

    public static <T> void registerFormatter(Class<T> clazz, ILogClassFormatter<T> formatter)
    {
        mFormatters.put(clazz, formatter);
    }
}