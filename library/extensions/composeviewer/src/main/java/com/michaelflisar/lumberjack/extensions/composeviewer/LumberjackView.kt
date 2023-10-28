package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    setup: IFileLoggingSetup,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    if (visible.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { visible.value = false }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                color = MaterialTheme.colorScheme.background
            ) {
                val scope = rememberCoroutineScope()
                val listState = rememberLazyListState()
                var showMenu by remember { mutableStateOf(false) }
                var showMenu2 by remember { mutableStateOf(false) }
                val logFile = rememberLogFile()
                val reload = rememberReloadFile()
                Column {
                    TopAppBar(
                        title = { Text(title) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    listState.scrollToItem(Int.MAX_VALUE)
                                }
                            }) {
                                Icon(Icons.Default.KeyboardArrowDown, null)
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    listState.scrollToItem(0)
                                }
                            }) {
                                Icon(Icons.Default.KeyboardArrowUp, null)
                            }
                            IconButton(onClick = {
                                showMenu = true
                            }) {
                                Icon(Icons.Default.MoreVert, null)
                                if (showMenu) {
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Reload File") },
                                            leadingIcon = { Icon(Icons.Default.Refresh, null) },
                                            onClick = {
                                                reload.value = true
                                                showMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete Log File(s)") },
                                            leadingIcon = { Icon(Icons.Default.Delete, null) },
                                            onClick = {
                                                scope.launch {
                                                    setup.clearLogFiles()
                                                    reload.value = true
                                                    showMenu = false
                                                }
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Select Log File") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.InsertDriveFile,
                                                    null
                                                )
                                            },
                                            onClick = {
                                                showMenu = false
                                                showMenu2 = true
                                            }
                                        )
                                    }
                                }
                                if (showMenu2) {
                                    val files = setup.getAllExistingLogFiles()
                                    DropdownMenu(
                                        expanded = showMenu2,
                                        onDismissRequest = { showMenu2 = false }
                                    ) {
                                        if (files.isEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text("NO FILES FOUND!") },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Error,
                                                        null
                                                    )
                                                },
                                                onClick = {
                                                    showMenu2 = false
                                                }
                                            )
                                        }
                                        files.forEach { file ->
                                            DropdownMenuItem(
                                                text = { Text(file.name) },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.InsertDriveFile,
                                                        null
                                                    )
                                                },
                                                onClick = {
                                                    logFile.value = file
                                                    reload.value = true
                                                    showMenu2 = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    LumberjackView(
                        setup,
                        file = logFile,
                        reload = reload,
                        modifier = Modifier.weight(1f),
                        state = listState,
                        darkTheme = darkTheme
                    )
                }
            }
        }
    }
}

@Composable
fun rememberLogFile(file: File? = null): MutableState<File?> {
    return rememberSaveable {
        mutableStateOf(file)
    }
}

@Composable
fun rememberReloadFile(reload: Boolean = true): MutableState<Boolean> {
    return rememberSaveable {
        mutableStateOf(reload)
    }
}

@Composable
fun LumberjackView(
    fileLoggingSetup: IFileLoggingSetup,
    file: MutableState<File?> = rememberLogFile(),
    reload: MutableState<Boolean> = rememberReloadFile(),
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val data = remember { mutableStateOf<Data>(Data.None) }
    LaunchedEffect(reload.value, file.value) {
        if (file.value == null) {
            file.value = fileLoggingSetup.getLatestLogFiles()
        }
        val f = file.value
        if (reload.value && f != null) {
            reload.value = false
            if (!f.exists()) {
                data.value = Data.FileNotFound
            } else {
                withContext(Dispatchers.IO) {
                    var lines = emptyList<String>()
                    try {
                        lines = f.readLines()
                    } catch (e: FileNotFoundException) {
                        // ignore
                    }
                    val logs = fileLoggingSetup.fileConverter.parseFile(lines)
                    data.value = Data.Loaded(logs)
                }
            }
        }
    }

    val filterOptions by remember {
        derivedStateOf { listOf(null) + Level.values().toList().filter { it.priority >= 0 } }
    }
    val filter = rememberSaveable {
        mutableStateOf(filterOptions.first())
    }
    val filter2 = rememberSaveable {
        mutableStateOf("")
    }

    val spanStyle = SpanStyle(
        background = MaterialTheme.colorScheme.primary,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold
    )

    val count by remember(data.value) {
        derivedStateOf { data.value.count }
    }
    val countFiltered by remember(count) {
        derivedStateOf { count }
    }

    Column(
        modifier = modifier
    ) {
        Filter(filter, filterOptions, filter2)
        Info(file.value, countFiltered, count)
        when (val d = data.value) {
            Data.FileNotFound -> TODO()
            is Data.Loaded -> {
                val filteredEntries by remember(d.entries, filter.value, filter2.value) {
                    derivedStateOf {
                        d.entries
                            .filter { entry ->
                                filter.value?.let { f -> entry.level.priority >= f.priority }
                                    ?: true
                            }
                            .filter { entry ->
                                filter2.value.takeIf { it.isNotEmpty() }?.let { f ->
                                    entry.lines.find { it.contains(f, true) } != null ||
                                            entry.level.name.contains(f, true)
                                } ?: true
                            }
                    }
                }
                LazyColumn(
                    state = state
                ) {
                    filteredEntries.forEach {
                        item(key = it.lineNumber) {
                            LogEntry(it, filter2.value, darkTheme, spanStyle)
                        }
                    }
                }
            }

            Data.None -> {
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Filter(
    filter: MutableState<Level?>,
    filterOptions: List<Level?>,
    filter2: MutableState<String>
) {
    var filterExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = filterExpanded,
            onExpandedChange = {
                filterExpanded = !filterExpanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = getLevelName(filter.value),
                singleLine = true,
                onValueChange = { value ->
                    filterOptions.find { getLevelName(it) == value }
                },
                label = { Text("Min Level") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = filterExpanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = filterExpanded,
                onDismissRequest = {
                    filterExpanded = false
                }
            ) {
                filterOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(text = getLevelName(option))
                        },
                        onClick = {
                            filter.value = option
                            filterExpanded = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.weight(2f),
            value = filter2.value,
            onValueChange = { filter2.value = it },
            label = { Text("Filter") }
        )
    }
}

private fun getLevelName(level: Level?): String {
    return level?.name ?: "ALL"
}

@Composable
private fun Info(file: File?, filteredCount: Int, totalCount: Int) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            text = file?.name ?: "",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            if (filteredCount == totalCount) "$filteredCount" else "$filteredCount/$totalCount",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private val INSET = 32.dp

@Composable
private fun LogEntry(
    entry: IFileConverter.Entry,
    filter: String,
    darkTheme: Boolean,
    spanStyle: SpanStyle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val level by remember(entry.level, filter) {
            derivedStateOf {
                getHighlightedText(entry.level.name, filter, true, spanStyle)
            }
        }
        Row {
            Text(
                text = "${entry.lineNumber}",
                modifier = Modifier.width(INSET),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = level,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = entry.level.getColor(darkTheme)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = entry.date, style = MaterialTheme.typography.labelSmall)
        }
        val plainText by remember(entry.lines) {
            derivedStateOf { entry.lines.joinToString("\n") }
        }
        val text by remember(plainText, filter) {
            derivedStateOf {
                getHighlightedText(plainText, filter, true, spanStyle)
            }
        }

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = INSET)
                .horizontalScroll(rememberScrollState()),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun getHighlightedText(
    text: String,
    search: String,
    ignoreCase: Boolean,
    spanStyle: SpanStyle
): AnnotatedString {
    if (text.isEmpty() || search.isEmpty())
        return buildAnnotatedString { append(text) }

    return buildAnnotatedString {
        var start = 0
        while (text.indexOf(search, start, ignoreCase = ignoreCase) != -1 && search.isNotBlank()) {
            val firstIndex = text.indexOf(search, start, true)
            val end = firstIndex + search.length
            append(text.substring(start, firstIndex))
            withStyle(style = spanStyle) {
                append(text.substring(firstIndex, end))
            }
            start = end
        }
        append(text.substring(start, text.length))
        toAnnotatedString()
    }
}

sealed class Data {
    abstract val count: Int

    class Loaded(val entries: List<IFileConverter.Entry>) : Data() {
        override val count: Int
            get() = entries.size
    }

    data object FileNotFound : Data() {
        override val count = 0
    }

    data object None : Data() {
        override val count = 0
    }
}
