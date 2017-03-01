package com.michaelflisar.lumberjack.formatter;

import android.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by flisar on 20.10.2016.
 */

public interface ILogFormatter
{
    String formatLine(String tag, String message);

    /*
    * for filtering by groups, it is necessary to extrect the group from the combined tag again
     */
    String extractGroupFromTag(String tag);
    /*
   * Timber supports one tag only, so the group and the stack trace tag needs to be combined
    */
    String combineTagAndGroup(String tag, String group);

    <T> String format(Collection<T> collection, HashMap<Class, ILogClassFormatter> formatters);
    <T> String format(T[] array, HashMap<Class, ILogClassFormatter> formatters);
    <T> Object format(T item, HashMap<Class, ILogClassFormatter> formatters, boolean inList);
}
