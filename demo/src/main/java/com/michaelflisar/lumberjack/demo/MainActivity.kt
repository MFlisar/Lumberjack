package com.michaelflisar.lumberjack.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.demo.databinding.ActivityMainBinding
import com.michaelflisar.lumberjack.sendFeedback
import com.michaelflisar.lumberjack.showLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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

        lifecycleScope.launch(Dispatchers.IO) {
            L.d { "6 - from within coroutine on background thread: ${Thread.currentThread()}" }
        }

        binding.btSendFeedback.setOnClickListener {
            val receiver = binding.tvReceiver.text.toString()
            receiver.takeIf { it.isNotEmpty() }?.let {
                L.sendFeedback(this, LogHelper.FILE_LOGGING_SETUP.getLatestLogFiles(), it)
            } ?: Toast.makeText(this, "You must provide a valid email address to test this function!", Toast.LENGTH_SHORT).show()
        }
        binding.btResetLogFiles.setOnClickListener {
            LogHelper.clearLogFiles()
            L.d { "Old log files deleted and newest log file cleared, this is the only line in the log file!" }
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

        for (i in 0..500) {
            L.d { "Test $i" }
        }


        lifecycleScope.launchWhenStarted {
            L.tag("LEVEL").v { "Verbose log..." }
            L.tag("LEVEL").d { "Debug log..." }
            L.tag("LEVEL").i { "Info log..." }
            L.tag("LEVEL").w { "Warn log..." }
            L.tag("LEVEL").e { "Error log..." }
            L.tag("LEVEL").wtf { "WTF log..." }
        }
    }

    private fun updateTheme() {
        AppCompatDelegate.setDefaultNightMode(if (App.darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private class TestException(msg: String) : Throwable(msg) {
        override fun toString(): String {
            return message!!
        }
    }
}