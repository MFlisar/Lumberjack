package com.michaelflisar.lumberjack.formatter;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by flisar on 20.10.2016.
 */

public interface ILogFormatter
{
    String formatLine(String tag, String message);

    <T> String format(Collection<T> collection, HashMap<Class, ILogClassFormatter> formatters);
    <T> String format(T item, HashMap<Class, ILogClassFormatter> formatters, boolean inList);
}
