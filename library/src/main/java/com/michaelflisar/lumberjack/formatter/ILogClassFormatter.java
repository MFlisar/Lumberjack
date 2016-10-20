package com.michaelflisar.lumberjack.formatter;

/**
 * Created by flisar on 20.10.2016.
 */

public interface ILogClassFormatter<T>
{
    String log(T item, boolean logInList);
}
