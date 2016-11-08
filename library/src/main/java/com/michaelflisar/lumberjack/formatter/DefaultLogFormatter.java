package com.michaelflisar.lumberjack.formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by flisar on 20.10.2016.
 */

public class DefaultLogFormatter implements ILogFormatter
{
    private Integer mMaxListValues;
    private boolean mEnableBeautifulCollectionPrint;
    private boolean mEnableCollectionPrintNewLines;

    public DefaultLogFormatter(Integer maxListValues, boolean enableBeautifulCollectionPrint, boolean enableCollectionPrintNewLines)
    {
        mEnableBeautifulCollectionPrint = enableBeautifulCollectionPrint;
        mEnableCollectionPrintNewLines = enableCollectionPrintNewLines;
        mMaxListValues = maxListValues;
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
    public String extractGroupFromTag(String tag)
    {
        if (!tag.startsWith("<"))
            return null;
        int first = tag.indexOf("<") + 1;
        int last = tag.indexOf(">");
        return tag.substring(first, last);
    }

    @Override
    public String combineTagAndGroup(String tag, String group)
    {
        return "<" + group + "> " + tag;
    }

    @Override
    public <T> String format(Collection<T> collection, HashMap<Class, ILogClassFormatter> formatters)
    {
        if (collection == null)
            return "Collection=NULL";
        if (collection.size() == 0)
            return "Collection=EMPTY";

        return formatArrayOrCollection(collection, formatters);
    }

    @Override
    public <T> String format(T[] array, HashMap<Class, ILogClassFormatter> formatters)
    {
        if (array == null)
            return "Array=NULL";
        if (array.length == 0)
            return "Array=EMPTY";

        return formatArrayOrCollection(Arrays.asList(array), formatters);
    }

    @Override
    public <T> Object format(T item, HashMap<Class, ILogClassFormatter> formatters, boolean inList)
    {
        if (item == null)
            return "NULL";

        ILogClassFormatter<T> formatter = (formatters == null || formatters.size() == 0) ? null : formatters.get(item.getClass());
        return formatter == null ? item : formatter.log(item, inList);
    }

    // ---------------------------
    // Helper function for arrays and collections
    // ---------------------------

    private <T> String formatArrayOrCollection(Collection<T> collection, HashMap<Class, ILogClassFormatter> formatters)
    {
        if (!mEnableBeautifulCollectionPrint)
            return collection.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("[Type=").append(collection.iterator().next().getClass().getSimpleName()).append(", Size=").append(collection.size()).append("]");
        if (mMaxListValues == null || mMaxListValues > 0)
        {
            if (mEnableCollectionPrintNewLines)
                sb.append("\n[");
            else
                sb.append("[");
            int count = 0;
            for (T item : collection)
            {
                if (mEnableCollectionPrintNewLines)
                    sb.append("\n\t");
                else if (count > 0)
                    sb.append(", ");

                sb.append(format(item, formatters, true));
                count++;
                if (mMaxListValues != null && count == mMaxListValues)
                    break;
            }
            if (mEnableCollectionPrintNewLines)
                sb.append("\n]");
            else
                sb.append("]");
        }

        return sb.toString();
    }
}