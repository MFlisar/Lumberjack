package com.michaelflisar.lumberjack.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.michaelflisar.lumberjack.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        L.d { "1 - MainActivity onCreate was just called" }
        L.logIf { false }?.d {
            // sleep 60s - no problem, this block will never be executed thanks to lazy evaluation
            Thread.sleep(1000 * 60)
            "2 - this log will never be printed nor will this block ever be executed"
        }
        L.e { "3 - Some error message" }
        L.e(TestException("4 - TestException"))

        // --------------
        // Specials
        // --------------

        val func = { info: String ->
            L.d { "5 - from within lambda: $info" }
        }

        func("func call 1...")
        func("func call 2...")
        func("func call 3...")

        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            L.d { "6 - from within coroutine on background thread: ${Thread.currentThread()}" }
        }
    }

    private class TestException(msg: String) : Throwable(msg) {
        override fun toString(): String {
            return message!!
        }
    }
}