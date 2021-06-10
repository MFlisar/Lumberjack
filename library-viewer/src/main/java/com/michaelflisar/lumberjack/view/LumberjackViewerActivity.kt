package com.michaelflisar.lumberjack.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.lumberjack.FileLoggingSetup
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.core.Level
import com.michaelflisar.lumberjack.interfaces.IDataExtractor
import com.michaelflisar.lumberjack.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.viewer.R
import com.michaelflisar.lumberjack.viewer.databinding.ActivityLumberjackViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class LumberjackViewerActivity : AppCompatActivity() {

    companion object {
        const val KEY_FILE = "FILE"
    }

    private lateinit var dataExtractor: IDataExtractor
    private lateinit var fileLoggingSetup: IFileLoggingSetup

    private var selectedFile: File? = null

    private lateinit var binding: ActivityLumberjackViewerBinding
    private val fileMenuItems: HashMap<MenuItem, File> = HashMap()

    private lateinit var logs: List<IDataExtractor.Data>
    private var adapter: LogAdapter? = null

    private var filterJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLumberjackViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        dataExtractor = intent.extras!!.getParcelable(LumberjackViewer.DATA_EXTRACTOR)!!
        fileLoggingSetup = intent.extras!!.getParcelable(LumberjackViewer.FILE_LOGGING_SETUP)!!

        if (savedInstanceState?.containsKey(KEY_FILE) == true) {
            selectedFile = savedInstanceState.getSerializable(KEY_FILE) as File
        }

        initList()
        initFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lumberjack_viewer_menu, menu)
        menu?.findItem(R.id.menu_select_file)?.subMenu?.let { subMenu ->
            fileLoggingSetup.getAllExistingLogFiles().forEachIndexed { index, file ->
                val menu = subMenu.add(0, index, Menu.NONE, file.name)
                fileMenuItems[menu] = file
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear_log_files -> {
                fileLoggingSetup.clearLogFiles()
                loadListData()
                true
            }
            R.id.menu_select_file -> {
                true
            }
            else -> {
                selectedFile = fileMenuItems[item]
                loadListData()
                true
            }
        }
    }

    private fun initList() {
        binding.rvLogs.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        loadListData()
    }

    // ----------------
    // helper functions
    // ----------------

    private fun loadListData() {
        binding.pbLoading.visibility = View.VISIBLE
        adapter?.clear()
        lifecycleScope.launch(Dispatchers.IO) {
            val lines = (selectedFile ?: fileLoggingSetup.getLatestLogFiles())?.readLines() ?: emptyList()

            val allLogs = ArrayList<IDataExtractor.Data>()
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
            withContext(Dispatchers.Main) {
                binding.pbLoading.visibility = View.GONE
                if (adapter == null) {
                    adapter = LogAdapter(this@LumberjackViewerActivity, logs, "")
                    binding.rvLogs.adapter = adapter
                } else {
                    adapter!!.update(logs, "")
                }
                updateFilter(true)
            }
        }
    }

    private fun createLogItem(existingEntries: Int, logEntry: String): IDataExtractor.Data? {
        return dataExtractor.extract(existingEntries, logEntry)
    }

    private fun initFilter() {
        binding.etFilter.doAfterTextChanged {
            updateFilter(false)
        }

        val items = mutableListOf("ALL")
        items.addAll(Level.values().filter { it.level != -1 }.map { ">= ${it.name}" })
        binding.spLevel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

        val loading = adapter == null
        val filter = binding.etFilter.text?.toString() ?: ""
        val level = binding.spLevel.selectedItemPosition.takeIf { it > 0 }
            ?.let { Level.values()[it - 1] }
        val filterIsActive = filter.isNotEmpty() || level != null

        L.d { "updateFilter: $init | $filter | $level | $filterIsActive" }

        if (loading || (init && !filterIsActive)) {
            updateInfos()
            return
        }

        filterJob?.cancel()

        if (!filterIsActive) {
            adapter!!.update(logs, filter)
            updateInfos()
            return
        }

        filterJob = lifecycleScope.launch(Dispatchers.IO) {
            val filtered = logs
                .filter {
                    (it.fullLogLine.contains(filter, true) || it.level.name.contains(
                        filter,
                        true
                    )) &&
                            (level == null || it.level.level >= level.level)
                }
            withContext(Dispatchers.Main) {
                adapter!!.update(filtered, filter)
                updateInfos()
            }
        }
    }

    private fun updateInfos() {
        binding.tvInfos.text = adapter?.let {
            "${it.items.size} / ${logs.size}"
        } ?: ""
    }
}