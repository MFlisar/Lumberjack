package com.michaelflisar.lumberjack.demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.demo.classes.DemoLibraryWithInternalLogger
import com.michaelflisar.lumberjack.demo.databinding.ActivityMainBinding
import com.michaelflisar.lumberjack.sendFeedback
import com.michaelflisar.lumberjack.showLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (updateTheme()) {
            // activity is recreated, we skip this on create...
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        L.d { "TEST-LOG - 1a - MainActivity onCreate was just called" }
        L.e(Exception("TEST")) { "TEST-LOG - 1b - MainActivity onCreate was just called" }
        L.logIf { true }?.d { "TEST-LOG - 1c - MainActivity onCreate was just called" }

        L.logIf { false }?.d {
            // sleep 60s - no problem, this block will never be executed thanks to lazy evaluation
            Thread.sleep(1000 * 60)
            "TEST-LOG - 2 - this log will never be printed nor will this block ever be executed"
        }
        L.e { "TEST-LOG - 3 - Some error message" }
        L.e(TestException("TEST-LOG - 4 - TestException (some info)"))

        // --------------
        // Specials
        // --------------

        val func = { info: String ->
            L.d { "TEST-LOG - 5 - from within lambda: $info" }
        }

        func("func call 1...")
        func("func call 2...")
        func("func call 3...")

        lifecycleScope.launch(Dispatchers.IO) {
            L.d { "TEST-LOG - 6 - from within coroutine on background thread: ${Thread.currentThread()}" }
        }

        binding.btSendFeedback.setOnClickListener {
            val receiver = binding.tvReceiver.text.toString()
            receiver.takeIf { it.isNotEmpty() }?.let {
                L.sendFeedback(this, LogHelper.FILE_LOGGING_SETUP.getLatestLogFiles(), it)
            } ?: Toast.makeText(this, "You must provide a valid email address to test this function!", Toast.LENGTH_SHORT).show()
        }
        binding.btResetLogFiles.setOnClickListener {
            LogHelper.clearLogFiles()
            L.d { "TEST-LOG - Old log files deleted and newest log file cleared, this is the only line in the log file!" }
        }
        binding.btShowLogFile.setOnClickListener {
            val receiver = binding.tvReceiver.text.toString()
            L.showLog(this, LogHelper.FILE_LOGGING_SETUP, receiver)
        }
        binding.cbDarkTheme.apply {
            isChecked = App.darkTheme
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != App.darkTheme) {
                    App.darkTheme = isChecked
                    updateTheme()
                }
            }
        }

        for (i in 0..10) {
            L.d { "TEST-LOG - Test $i" }
        }

        lifecycleScope.launchWhenStarted {
            L.tag("LEVEL").v { "TEST-LOG - Verbose log..." }
            L.tag("LEVEL").d { "TEST-LOG - Debug log..." }
            L.tag("LEVEL").i { "TEST-LOG - Info log..." }
            L.tag("LEVEL").w { "TEST-LOG - Warn log..." }
            L.tag("LEVEL").e { "TEST-LOG - Error log..." }
            L.tag("LEVEL").wtf { "TEST-LOG - WTF log..." }
        }

        // call stack correction inside forwarded logger
        // this should log THIS line inside the custom logger
        DemoLibraryWithInternalLogger.run()

        // Logging with level
        L.tag("MANUAL LEVEL").log(Log.DEBUG) { "Debug log..." }
        L.tag("MANUAL LEVEL").log(Log.ERROR, Exception("EX")) { "Error log..." }
    }

    private fun updateTheme() : Boolean {
        val isDark = Util.isNightMode(this)
        return if (isDark != App.darkTheme) {
            AppCompatDelegate.setDefaultNightMode(if (App.darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            true
        } else false
    }

    private class TestException(msg: String) : Throwable(msg) {
        override fun toString(): String {
            return message!!
        }
    }
}