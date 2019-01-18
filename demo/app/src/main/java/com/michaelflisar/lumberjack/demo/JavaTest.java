package com.michaelflisar.lumberjack.demo;

import com.michaelflisar.lumberjack.L2;

public class JavaTest
{
    public static void test()
    {
        // USE L2 in JAVA code, otherwise callstack won't be correct because of missing inlining functionality in java!
        L2.d(new Throwable(), "JAVA 1 - ERROR");

        L2.d("JAVA 2 - Test message");
        L2.d("JAVA 3 - Test message with arg: %d", 500);
        L2.e(new Throwable("ERROR"), "JAVA 4 - Test error");

        L2.tag("CUSTOM-TAG").d("JAVA 5 - Test message with custom tag");
    }
}
