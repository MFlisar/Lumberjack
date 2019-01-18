package com.michaelflisar.lumberjack.demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.T

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count")
        }

        findViewById<View>(R.id.btLog).setOnClickListener(this)
        findViewById<View>(R.id.btLogError).setOnClickListener(this)

        // Test 1: a few simple test messages
        L.d { "1 - Main activity created" }
        L.d { "2 - Test message - count: $count" }
        L.e(Throwable("ERROR"), { "3 - Test error" })
        L.tag("CUSTOM-TAG").d { "4 - Test message with custom tag" }

        // Test 2: test logging based on boolean flags / boolen function
        L.logIf { true }?.d { "5 - Test log based on boolen flag " }
        L.logIf { false }?.d { "6 - THIS should never be logged, no even evaluated! " }
        L.logIf { false }?.d {
            Thread.sleep(15000)
            "7 - THREAD SLEEP will never run, so it is safe to run slow debug operations here!"
        }

        // this will show that the thread sleep above is never executed!
        Toast.makeText(this, "onCreate finished", Toast.LENGTH_SHORT).show()

        // Test 3: T (Time) class example
        val timer1 = "TIMER-1"
        Thread {
            T.start(timer1)
            Thread.sleep(500)
            T.lap(timer1)
            Thread.sleep(1000)
            T.stop(timer1)
            L.d { "8 - Timer data: ${T.print(timer1)}" }
        }.run()

        JavaTest.test()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("count", count)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btLog) {
            count++
            L.d { "Button clicked: $count" }
        } else {
            L.e { "Error message" }
        }
    }
}
