package com.michaelflisar.lumberjack;

import android.util.Log;
import android.util.Pair;

import com.michaelflisar.lumberjack.formatter.DefaultLogFormatter;
import com.michaelflisar.lumberjack.formatter.ILogClassFormatter;
import com.michaelflisar.lumberjack.formatter.ILogFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
* Offers a shorter interfaces (L instead of Timber) for a more clean and shorter code
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
            mLogFormatter = new DefaultLogFormatter(5, true, true);
            //throw new RuntimeException("You did not initialise the logger's formatter!");
        return mLogFormatter;
    }

    public static void setLogFormatter(ILogFormatter formatter)
    {
        mLogFormatter = formatter;
    }

    // --------------------
    // Logging - Groups and call stack correction
    // --------------------

    public static LogBuilder withGroup(String group)
    {
        return new LogBuilder().withGroup(group).withDecreaseCallStackCorrection();
    }

    public static LogBuilder withCallStackCorrection(int correction)
    {
        return new LogBuilder().withCallStackCorrection(correction).withDecreaseCallStackCorrection();
    }

    public static LogBuilder onlyIf(boolean enable)
    {
        return new LogBuilder().withIf(enable).withDecreaseCallStackCorrection();
    }
    
    // --------------------
    // Logging - VERBOSE - direct forwards to builder
    // --------------------

    public static void v(String message, Object... args)
    {
        new LogBuilder().v(message, args);
    }

    public static void v(Throwable t, String message, Object... args)
    {
        new LogBuilder().v(t, message, args);
    }

    public static void v(Throwable t)
    {
        new LogBuilder().v(t);
    }

    public static void v(LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().v(builder);
    }

    // --------------------
    // Logging - DEBUG
    // --------------------

    public static void d(String message, Object... args)
    {
        new LogBuilder().d(message, args);
    }

    public static void d(Throwable t, String message, Object... args)
    {
        new LogBuilder().d(t, message, args);
    }

    public static void d(Throwable t)
    {
        new LogBuilder().d(t);
    }

    public static void d(LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().d(builder);
    }

    // --------------------
    // Logging - INFO
    // --------------------

    public static void i(String message, Object... args)
    {
        new LogBuilder().i(message, args);
    }

    public static void i(Throwable t, String message, Object... args)
    {
        new LogBuilder().i(t, message, args);
    }

    public static void i(Throwable t)
    {
        new LogBuilder().i(t);
    }

    public static void i(LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().i(builder);
    }

    // --------------------
    // Logging - WARN
    // --------------------

    public static void w(String message, Object... args)
    {
        new LogBuilder().w(message, args);
    }

    public static void w(Throwable t, String message, Object... args)
    {
        new LogBuilder().w(t, message, args);
    }

    public static void w(Throwable t)
    {
        new LogBuilder().w(t);
    }

    public static void w(LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().w(builder);
    }

    // --------------------
    // Logging - Error
    // --------------------

    public static void e(String message, Object... args)
    {
        new LogBuilder().e(message, args);
    }

    public static void e(Throwable t, String message, Object... args)
    {
        new LogBuilder().e(t, message, args);
    }

    public static void e(Throwable t)
    {
        new LogBuilder().e(t);
    }

    public static void e(LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().e(builder);
    }

    // --------------------
    // Logging - Priority
    // --------------------

    public static void log(int priority, String message, Object... args)
    {
        new LogBuilder().log(priority, message, args);
    }

    public static void log(int priority, Throwable t, String message, Object... args)
    {
        new LogBuilder().log(priority, t, message, args);
    }

    public static void log(int priority, Throwable t)
    {
        new LogBuilder().log(priority, t);
    }

    public static void log(int priority, LogLabelValuePairsBuilder builder)
    {
        new LogBuilder().log(priority, "%s", builder.prepareLog());
    }

    // --------------------
    // Formatter
    // --------------------

    private static void updateTag(String group)
    {
        if (group == null)
            Timber.tag(null);
        else
            Timber.tag(group);
    }

    private static void updateStackDepth(Integer correction)
    {
        if (correction == null)
            Lumberjack.callStackCorrection(null);
        else
            Lumberjack.callStackCorrection(correction);
    }

    public static Object formatArg(Object arg)
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
        if (args == null || args.length == 0)
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
    // Builder and alikes
    // --------------------

    public static class LogBuilder
    {
        boolean enable = true;
        String group = null;
        Integer callStackCorrection = null;

        LogBuilder()
        {
        }

        // --------------------
        // public builder
        // --------------------

        public LogBuilder withGroup(String group)
        {
            this.group = group;
            return this;
        }

        public LogBuilder withIf(boolean enable)
        {
            this.enable = enable;
            return this;
        }

        public LogBuilder withCallStackCorrection(int correction)
        {
            if (callStackCorrection == null)
                this.callStackCorrection = correction;
            else
                this.callStackCorrection += correction;
            return this;
        }

        public LogBuilder withOverwriteCallStackCorrection(int correction)
        {
            this.callStackCorrection = correction;
            return this;
        }

        LogBuilder withDecreaseCallStackCorrection()
        {
            if (callStackCorrection == null)
                callStackCorrection = -1;
            else
                callStackCorrection++;
            return this;
        }

        // --------------------
        // VERBOSE - public logging functions
        // --------------------
        
        public void v(String message, Object... args)
        {
            log(Log.VERBOSE, message, args);
        }

        public void v(Throwable t, String message, Object... args)
        {
            log(Log.VERBOSE, t, message, args);
        }

        public void v(Throwable t)
        {
            log(Log.VERBOSE, t);
        }
        
        public void v(LogLabelValuePairsBuilder builder)
        {
            log(Log.VERBOSE, "%s", builder.prepareLog());
        }

        // --------------------
        // DEBUG - public logging functions
        // --------------------

        public void d(String message, Object... args)
        {
            log(Log.DEBUG, message, args);
        }

        public void d(Throwable t, String message, Object... args)
        {
            log(Log.DEBUG, t, message, args);
        }

        public void d(Throwable t)
        {
            log(Log.DEBUG, t);
        }

        public void d(LogLabelValuePairsBuilder builder)
        {
            log(Log.DEBUG, "%s", builder.prepareLog());
        }

        // --------------------
        // INFO - public logging functions
        // --------------------

        public void i(String message, Object... args)
        {
            log(Log.INFO, message, args);
        }

        public void i(Throwable t, String message, Object... args)
        {
            log(Log.INFO, t, message, args);
        }

        public void i(Throwable t)
        {
            log(Log.INFO, t);
        }

        public void i(LogLabelValuePairsBuilder builder)
        {
            log(Log.INFO, "%s", builder.prepareLog());
        }

        // --------------------
        // WARN - public logging functions
        // --------------------

        public void w(String message, Object... args)
        {
            log(Log.WARN, message, args);
        }

        public void w(Throwable t, String message, Object... args)
        {
            log(Log.WARN, t, message, args);
        }

        public void w(Throwable t)
        {
            log(Log.WARN, t);
        }

        public void w(LogLabelValuePairsBuilder builder)
        {
            log(Log.WARN, "%s", builder.prepareLog());
        }

        // --------------------
        // WARN - public logging functions
        // --------------------

        public void e(String message, Object... args)
        {
            log(Log.ERROR, message, args);
        }

        public void e(Throwable t, String message, Object... args)
        {
            log(Log.ERROR, t, message, args);
        }

        public void e(Throwable t)
        {
            log(Log.ERROR, t);
        }

        public void e(LogLabelValuePairsBuilder builder)
        {
            log(Log.ERROR, "%s", builder.prepareLog());
        }

        // --------------------
        // REAL public logging functions
        // --------------------

        public void log(int priority, String message, Object... args)
        {
            if (!enable)
                return;

            // 1) set tag
            updateTag(group);

            // 2) set call stack correction
            updateStackDepth(callStackCorrection);

            // 3) log
            Timber.log(priority, message, formatArgs(args));
        }

        public void log(int priority, Throwable t, String message, Object... args)
        {
            // 1) set tag
            updateTag(group);

            // 2) set call stack correction
            updateStackDepth(callStackCorrection);

            // 3) log
            Timber.log(priority, t, message, formatArgs(args));
        }

        public void log(int priority, Throwable t)
        {
            // 1) set tag
            updateTag(group);

            // 2) set call stack correction
            updateStackDepth(callStackCorrection);

            // 3) log
            Timber.log(priority, t);
        }
    }

    public static LogLabelValuePairsBuilder labeledValueBuilder()
    {
        return new LogLabelValuePairsBuilder();
    }

    public static class LogLabelValuePairsBuilder
    {
        List<Pair<String, Object>> pairs;

        LogLabelValuePairsBuilder()
        {
            pairs = new ArrayList<>();
        }

        public LogLabelValuePairsBuilder addPair(String label, Object value)
        {
            if (label == null)
                throw new RuntimeException("Labels can't be NULL, that makes no sense!");
            pairs.add(new Pair<>(label, value));
            return this;
        }

        String prepareLog()
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pairs.size(); i++)
            {
                if (i > 0)
                    sb.append(", ");
                sb.append(pairs.get(i).first).append("=");
                if (mFormatters.size() == 0)
                    sb.append(pairs.get(i).second);
                else
                    sb.append(formatArg(pairs.get(i).second));
            }
            return sb.toString();
        }
    }
}