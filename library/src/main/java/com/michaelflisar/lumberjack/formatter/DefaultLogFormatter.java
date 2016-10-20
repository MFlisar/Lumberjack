package com.michaelflisar.lumberjack.formatter;

import com.michaelflisar.lumberjack.L;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by flisar on 20.10.2016.
 */

public class DefaultLogFormatter implements ILogFormatter
{
    private int mMaxListValues;
    private boolean mEnableBeautifulCollectionPrint;

    public DefaultLogFormatter(int maxListValues)
    {
        mEnableBeautifulCollectionPrint = true;
        mMaxListValues = maxListValues;
    }

    public DefaultLogFormatter(boolean enableBeautifulCollectionPrint)
    {
        mEnableBeautifulCollectionPrint = true;
        mMaxListValues = 0;
    }

    // -----------------------
    // Interface
    // -----------------------

    @Override
    public String formatLine(String tag, String message)
    {
        return "[" + tag + "]: " + message;
    }

    @Override
    public <T> String format(Collection<T> collection, HashMap<Class, ILogClassFormatter> formatters)
    {
        if (collection == null)
            return "Collection=NULL";
        if (collection.size() == 0)
            return "Collection=EMPTY";

        if (!mEnableBeautifulCollectionPrint)
            return collection.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("[Type=").append(collection.iterator().next().getClass().getSimpleName()).append(", Size=").append(collection.size()).append("] ");
        if (mMaxListValues > 0)
        {
            sb.append("[");
            int count = 0;
            for (T item : collection)
            {
                if (count > 0)
                    sb.append(", ");
                sb.append(format(item, formatters, true));
                count++;
                if (count == mMaxListValues)
                    break;
            }
            sb.append("]");
        }

        return sb.toString();
    }

    @Override
    public <T> String format(T item, HashMap<Class, ILogClassFormatter> formatters, boolean inList)
    {
        if (item == null)
            return "NULL";

        ILogClassFormatter<T> formatter = (formatters == null || formatters.size() == 0) ? null : formatters.get(item.getClass());
        return formatter == null ? item.toString() : formatter.log(item, inList);
    }
}
