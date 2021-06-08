package com.michaelflisar.lumberjack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.lumberjack.viewer.databinding.ActivityLumberjackViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.log

internal class LumberjackViewerActivity : AppCompatActivity() {

    companion object {

        val FILE = "FILE"

        fun show(context: Context, file: File) {
            context.startActivity(createIntent(context, file))
        }

        fun createIntent(context: Context, file: File) : Intent {
            return Intent(
                context,
                LumberjackViewerActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(FILE, file)
            }
        }
    }

    private var loading: Boolean = true
    private lateinit var binding: ActivityLumberjackViewerBinding
    private lateinit var adapter: LogAdapter
    private lateinit var logs: List<LogAdapter.Item>
    private var filterJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLumberjackViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initList()
        initFilter()
    }

    private fun initList() {
        binding.rvLogs.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val file = intent.extras!!.getSerializable(FILE) as File
        lifecycleScope.launch(Dispatchers.IO) {
            val lines = file.readLines()

            val allLogs = ArrayList<LogAdapter.Item>()
            var logEntry: String? = null
            lines.forEach {
                if (logEntry == null) {
                    logEntry = it
                } else {
                    // log lines that are intented do belong to the parent log line (e.g. an exception)
                    if (it.firstOrNull()?.isWhitespace() == true) {
                        logEntry += "\n" + it
                    } else {
                        val item = createLogItem(allLogs.size, logEntry!!)
                        item?.let { allLogs.add(it) }
                        logEntry = it
                    }
                }
            }

            val item = logEntry?.let { createLogItem(allLogs.size, it) }
            item?.let { allLogs.add(item) }

            logs = allLogs

            adapter = LogAdapter(logs, "")
            loading = false
            withContext(Dispatchers.Main) {
                binding.rvLogs.adapter = adapter
                updateFilter(binding.etFilter.text.toString(), true)
            }
        }
    }

    private fun createLogItem(existingEntry: Int, logEntry: String): LogAdapter.Item? {
        if (logEntry.trim().isNotEmpty()) {
            var level: LogAdapter.Item.Level = LogAdapter.Item.Level.UNKNOWN

            // we try to get log level from default file logging format
            // e.g. 2000-01-01 00:00:00.000 INFO Some log
            // => 23 chars (including 1 space) + 2nd space + TAG + 3rd space + rest
            if (logEntry.count { it == ' ' } > 3)
            {
                val ind1 = logEntry.indexOf(' ')
                val ind2 = logEntry.indexOf(' ', ind1 + 1)
                val ind3 = logEntry.indexOf(' ', ind2 + 1)
                val levelString = logEntry.substring(ind2, ind3).trim()
                level = LogAdapter.Item.Level.values().find { it.name == levelString } ?:  LogAdapter.Item.Level.UNKNOWN
            }
            return LogAdapter.Item(existingEntry, logEntry, level)
        }
        return null
    }

    private fun initFilter() {
        binding.etFilter.doAfterTextChanged {
            updateFilter(it?.toString() ?: "", false)
        }
    }

    private fun updateFilter(filter: String, init: Boolean) {

        if (loading || (init && filter.isEmpty())) {
            updateInfos()
            return
        }

        filterJob?.cancel()

        if (filter.isEmpty()) {
            adapter.update(logs, filter)
            updateInfos()
            return
        }

        filterJob = lifecycleScope.launch(Dispatchers.IO) {
            val filtered = logs.filter { it.text.contains(filter, true) || it.level.name.contains(filter, true) }
            withContext(Dispatchers.Main) {
                adapter.update(filtered, filter)
                updateInfos()
            }
        }
    }

    private fun updateInfos() {
        binding.tvInfos.text = "${adapter.items.size} / ${logs.size}"
    }
}