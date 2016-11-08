package com.michaelflisar.lumberjack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by flisar on 20.10.2016.
 */

public class LogUtil
{
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    public static StackData getStackData(int stackIndex)
    {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= stackIndex + 1) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        return getStackData(stackTrace[stackIndex + 1]);
    }

    private static StackData getStackData(StackTraceElement element)
    {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return new StackData(tag, element.getMethodName(), element.getLineNumber());
    }

    public static class StackData
    {
        private final String mClassName;
        private final String mSimpleClassName;
        private final String mMethodName;
        private final int mLine;

        public StackData(String className, String methodName, int line)
        {
            mClassName = className;
            mSimpleClassName = mClassName.substring(mClassName.lastIndexOf('.') + 1);
            mMethodName = methodName;
            mLine = line;
        }

        public String getStackTag()
        {
            return mSimpleClassName + ":" + mLine + " " + mMethodName;
        }

        public String getLink()
        {
            return mSimpleClassName + ".java:" + mLine;
        }

        public String appendLink(String source)
        {
            String[] lines = source.split("\r\n|\r|\n");
            if (lines.length <= 1)
                return source + " (" + getLink() + ")";
            // this makes sure that links always works, like for example if pretty print for collections is enabled
            else
            {
                lines[0] = lines[0] + " (" + getLink() + ")";
                StringBuilder builder = new StringBuilder();
                for(String s : lines)
                    builder.append(s).append("\n");
                return builder.toString();
            }
        }
    }
}