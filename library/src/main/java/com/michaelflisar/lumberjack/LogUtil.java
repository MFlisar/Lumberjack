package com.michaelflisar.lumberjack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by flisar on 20.10.2016.
 */

public class LogUtil
{
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    public static String getStackTag(int stackIndex)
    {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= stackIndex + 1) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        return createStackElementTag(stackTrace[stackIndex + 1]);
    }

    private static String createStackElementTag(StackTraceElement element)
    {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1) + "-" + element.getLineNumber() + " " + element.getMethodName() + "";
    }
}
