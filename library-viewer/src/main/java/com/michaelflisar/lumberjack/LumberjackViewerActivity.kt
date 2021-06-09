package com.michaelflisar.lumberjack

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

internal class LumberjackViewerActivity : AppCompatActivity() {

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
        val file = intent.extras!!.getSerializable(LumberjackViewer.FILE) as File
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

            adapter = LogAdapter(this@LumberjackViewerActivity, logs, "")
            loading = false
            withContext(Dispatchers.Main) {
                binding.rvLogs.adapter = adapter
                updateFilter(true)
            }
        }
    }

    private fun createLogItem(existingEntry: Int, logEntry: String): LogAdapter.Item? {
        if (logEntry.trim().isNotEmpty()) {
            var date: String? = null
            var level: LogAdapter.Item.Level = LogAdapter.Item.Level.UNKNOWN

            // we try to get log level from default file logging format
            // e.g. 2000-01-01 00:00:00.000 INFO Some log
            // => 23 chars (including 1 space) + 2nd space + TAG + 3rd space + rest
            if (logEntry.count { it == ' ' } > 3) {
                val ind1 = logEntry.indexOf(' ')
                val ind2 = logEntry.indexOf(' ', ind1 + 1)
                val ind3 = logEntry.indexOf(' ', ind2 + 1)
                val levelString = logEntry.substring(ind2, ind3).trim()
                date = logEntry.substring(0, ind2).trim()
                level = LogAdapter.Item.Level.values().find { it.name == levelString }
                    ?: LogAdapter.Item.Level.UNKNOWN
            }
            return LogAdapter.Item(existingEntry, logEntry, level, date)
        }
        return null
    }

    private fun initFilter() {
        binding.etFilter.doAfterTextChanged {
            updateFilter(false)
        }

        val items = mutableListOf("ALL")
        items.addAll(LogAdapter.Item.Level.values().filter { it.level != -1 }.map { it.name })
        binding.spLevel.adapter = ArrayAdapter(this, R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
        binding.spLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateFilter(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateFilter(init: Boolean) {

        val filter = binding.etFilter.text?.toString() ?: ""
        val level = binding.spLevel.selectedItemPosition.takeIf { it > 0 }
            ?.let { LogAdapter.Item.Level.values()[it - 1] }
        val filterIsActive = filter.isNotEmpty() || level != null

        L.d { "updateFilter: $init | $filter | $level | $filterIsActive" }

        if (loading || (init && !filterIsActive)) {
            updateInfos()
            return
        }

        filterJob?.cancel()

        if (!filterIsActive) {
            adapter.update(logs, filter)
            updateInfos()
            return
        }

        filterJob = lifecycleScope.launch(Dispatchers.IO) {
            val filtered = logs
                .filter {
                    (it.text.contains(filter, true) || it.level.name.contains(filter, true)) &&
                            (level == null || it.level.level >= level.level)
                }
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