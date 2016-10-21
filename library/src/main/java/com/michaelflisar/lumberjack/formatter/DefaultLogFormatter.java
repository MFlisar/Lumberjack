package com.michaelflisar.lumberjack.formatter;

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

    @Override
    public <T> Object format(T item, HashMap<Class, ILogClassFormatter> formatters, boolean inList)
    {
        if (item == null)
            return "NULL";

        ILogClassFormatter<T> formatter = (formatters == null || formatters.size() == 0) ? null : formatters.get(item.getClass());
        return formatter == null ? item : formatter.log(item, inList);
    }
}
