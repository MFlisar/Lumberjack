package com.michaelflisar.lumberjack.demo;

import com.michaelflisar.lumberjack.L;

public class JavaTest
{
    public static void test()
    {
        L.d(new Throwable());

        L.d("JAVA 1 - Test message");
        L.d("JAVA 2 - Test message with arg: %d", 500);
        L.e(new Throwable("ERROR"), "JAVA 3 - Test error");

        L.tag("CUSTOM-TAG").d("JAVA 4 - Test message with custom tag");
    }
}
