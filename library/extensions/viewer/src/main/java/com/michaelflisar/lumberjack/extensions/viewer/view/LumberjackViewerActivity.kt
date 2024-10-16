package com.michaelflisar.lumberjack.extensions.viewer.view

import android.content.Context
import android.content.Intent
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
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.classes.priority
import com.michaelflisar.lumberjack.core.getAllExistingLogFiles
import com.michaelflisar.lumberjack.core.getLatestLogFile
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.R
import com.michaelflisar.lumberjack.extensions.viewer.databinding.ActivityLumberjackViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

internal class LumberjackViewerActivity : AppCompatActivity() {

    companion object {
        const val KEY_FILE = "FILE"

        const val FILE_LOGGING_SETUP = "FILE-LOGGING_SETUP"
        const val TITLE = "TITLE"
        const val MAIL = "MAIL"
        const val THEME = "THEME"

        fun show(
            context: Context,
            fileLoggingSetup: IFileLoggingSetup,
            receiver: String?,
            title: String? = null,
            theme: Int? = null
        ) {
            context.startActivity(
                createIntent(
                    context,
                    fileLoggingSetup,
                    receiver,
                    title,
                    theme
                )
            )
        }

        fun createIntent(
            context: Context,
            fileLoggingSetup: IFileLoggingSetup,
            receiver: String?,
            title: String?,
            theme: Int? = null
        ): Intent {
            return Intent(
                context,
                LumberjackViewerActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(FILE_LOGGING_SETUP, fileLoggingSetup)
                receiver?.let {
                    putExtra(MAIL, it)
                }
                title?.let {
                    putExtra(TITLE, it)
                }
                theme?.let {
                    putExtra(THEME, it)
                }
            }
        }
    }

    private lateinit var fileLoggingSetup: IFileLoggingSetup
    private var receiver: String? = null

    private var selectedFile: File? = null

    private lateinit var binding: ActivityLumberjackViewerBinding
    private val fileMenuItems: HashMap<MenuItem, File> = HashMap()

    private lateinit var logs: List<IFileConverter.Entry>
    private var adapter: LogAdapter? = null

    private var filterJob: Job? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = if (intent.extras!!.containsKey(THEME)) intent.extras!!.getInt(THEME) else null
        theme?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        binding = ActivityLumberjackViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val title =
            if (intent.extras!!.containsKey(TITLE)) intent.extras!!.getString(TITLE)!! else null
        title?.let {
            supportActionBar?.title = it
        }
        receiver =
            if (intent.extras!!.containsKey(MAIL)) intent.extras!!.getString(MAIL)!! else null

        fileLoggingSetup = intent.extras!!.getParcelable(FILE_LOGGING_SETUP)!!

        if (savedInstanceState?.containsKey(KEY_FILE) == true) {
            selectedFile = savedInstanceState.getSerializable(KEY_FILE) as File
        } else {
            selectedFile = fileLoggingSetup.getLatestLogFile()
        }

        initList()
        initFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lumberjack_viewer_menu, menu)
        menu.findItem(R.id.menu_select_file)?.subMenu?.let { subMenu ->
            fileLoggingSetup.getAllExistingLogFiles()
                .sortedBy { it.name }
                .forEachIndexed { index, file ->
                    val m = subMenu.add(0, index, Menu.NONE, file.name)
                    fileMenuItems[m] = file
                }
        }
        menu.findItem(R.id.menu_send_log_file)?.isVisible = receiver?.isNotEmpty() == true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_scroll_to_top -> {
                binding.rvLogs.adapter?.itemCount?.takeIf { it > 0 }?.let {
                    binding.rvLogs.layoutManager?.scrollToPosition(0)
                }
                true
            }

            R.id.menu_scroll_to_bottom -> {
                binding.rvLogs.adapter?.itemCount?.minus(1)?.takeIf { it >= 0 }?.let {
                    binding.rvLogs.layoutManager?.scrollToPosition(it)
                }
                true
            }

            R.id.menu_reload_file -> {
                loadListData()
                true
            }

            R.id.menu_clear_log_files -> {
                lifecycleScope.launch {
                    fileLoggingSetup.clearLogFiles()
                    loadListData()
                }
                true
            }

            R.id.menu_send_log_file -> {
                // receiver must be valid if this menu entry is visible
                L.sendFeedback(this, receiver!!, attachments = listOfNotNull(selectedFile))
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
            val file = selectedFile
            var lines = emptyList<String>()
            try {
                lines = file?.readLines() ?: emptyList()
            } catch (e: FileNotFoundException) {
                // ignore
            }

            logs = fileLoggingSetup.fileConverter.parseFile(lines)

            withContext(Dispatchers.Main) {
                binding.pbLoading.visibility = View.GONE
                binding.tvFile.text = file?.name
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

    private fun initFilter() {
        binding.etFilter.doAfterTextChanged {
            updateFilter(false)
        }

        binding.tilFilter.setEndIconOnClickListener {
            binding.etFilter.setText("")
        }

        val items = mutableListOf("ALL")
        items.addAll(Level.entries.filter { it.priority != -1 }.map { ">= ${it.name}" })
        binding.spLevel.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
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
            ?.let { Level.entries[it - 1] }
        val filterIsActive = filter.isNotEmpty() || level != null

        //L.d { "updateFilter: $init | $filter | $level | $filterIsActive" }

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
                    (it.lines.find { it.contains(filter, true) } != null || it.level.name.contains(
                        filter,
                        true
                    )) &&
                            (level == null || it.level.priority >= level.priority)
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