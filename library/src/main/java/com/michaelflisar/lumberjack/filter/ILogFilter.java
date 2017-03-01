package com.michaelflisar.lumberjack.filter;

/**
 * Created by flisar on 01.03.2017.
 */

public interface ILogFilter
{
    boolean valid(String group, int priority);
}
