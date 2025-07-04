package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine

object LumberjackView {
    class Style internal constructor(
        val useAlternatingRowColors: Boolean,
        val color1: Color,
        val color2: Color,
        val singleScrollableLineView: Boolean
    )
}

object LumberjackViewDefaults {

    @Composable
    fun style(
        useAlternatingRowColors: Boolean = true,
        color1: Color = MaterialTheme.colorScheme.background,
        color2: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1f),
        singleScrollableLineView: Boolean = false
    ) = LumberjackView.Style(
        useAlternatingRowColors = useAlternatingRowColors,
        color1 = color1,
        color2 = color2,
        singleScrollableLineView = singleScrollableLineView
    )

}

@Composable
fun rememberLogFile(file: Path? = null): MutableState<Path?> {
    val SAVER = Saver<MutableState<Path?>, String>(
        save = { it.value?.toString() },
        restore = { mutableStateOf(it.takeIf { it.isNotEmpty() }?.let { Path(it) }) }
    )
    return rememberSaveable(
        saver = SAVER
    ) {
        mutableStateOf(file)
    }
}

@Composable
fun rememberLogFileData(data: Data = Data.Reload): MutableState<Data> {
    return remember { mutableStateOf(data) }
}

@Composable
fun LumberjackView(
    setup: IFileLoggingSetup,
    modifier: Modifier = Modifier,
    file: MutableState<Path?> = rememberLogFile(),
    data: MutableState<Data> = rememberLogFileData(),
    state: LazyListState = rememberLazyListState(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    style: LumberjackView.Style = LumberjackViewDefaults.style(),
    useScrollableLines: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    LaunchedEffect(data.value, file.value) {
        if (file.value == null) {
            file.value = setup.getLatestLogFilePath()
        }
        val f = file.value
        if (data.value == Data.Reload) {
            if (f != null) {
                if (!SystemFileSystem.exists(f)) {
                    data.value = Data.FileNotFound
                } else {
                    withContext(Dispatchers.IO) {
                        try {
                            val logs = setup.fileConverter.parseFile(readLines(f))
                            data.value = Data.Loaded(logs)
                        } catch (e: FileNotFoundException) {
                            data.value = Data.FileNotFound
                        }
                    }
                }
            } else {
                data.value = Data.FileNotFound
            }
        }
    }

    val filterOptions by remember {
        derivedStateOf { listOf(null) + Level.entries.filter { it.order >= 0 } }
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
    var countFiltered by remember(count) {
        mutableIntStateOf(-1)
    }

    Column(
        modifier = modifier
    ) {
        Filter(filter, filterOptions, filter2)
        Info(file.value, countFiltered, count)
        when (val d = data.value) {
            Data.FileNotFound -> {
                InfoState("File not found!")
            }

            is Data.Loaded -> {
                val filteredEntries by remember(d.entries, filter.value, filter2.value) {
                    derivedStateOf {
                        d.entries
                            .filter { entry ->
                                filter.value?.let { f -> entry.level.order >= f.order }
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
                LaunchedEffect(filteredEntries) {
                    countFiltered = filteredEntries.size
                    state.scrollToItem((filteredEntries.size - 1).coerceAtLeast(0))
                }
                if (d.entries.isEmpty()) {
                    InfoState("File is empty!")
                } else if (filteredEntries.isEmpty()) {
                    InfoState("Nothing matches the filter!")
                } else {
                    LazyScrollContainer(
                        state = state
                    ) {
                        filteredEntries.forEach {
                            item(key = it.lineNumber) {
                                LogEntry(
                                    it,
                                    filter2.value,
                                    darkTheme,
                                    spanStyle,
                                    style,
                                    useScrollableLines
                                )
                            }
                        }
                    }
                }
            }

            Data.Reload -> {
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
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
            label = { Text("Filter") },
            trailingIcon = if (filter2.value.isEmpty()) null else {
                {
                    IconButton(
                        onClick = { filter2.value = "" }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }
}

private fun getLevelName(level: Level?): String {
    return level?.name ?: "ALL"
}

@Composable
private fun Info(file: Path?, filteredCount: Int, totalCount: Int) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        val size = file?.let { SystemFileSystem.metadataOrNull(it) }?.size ?: 0L
        val info = FileSizeUtil.humanReadableBytes(size)
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            text = file?.let { "${it.name} ($info)" } ?: "",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            if (filteredCount == totalCount || filteredCount == -1) "$totalCount" else "$filteredCount/$totalCount",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ColumnScope.InfoState(info: String) {
    Text(
        text = info,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(16.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}

private val INSET = 32.dp

@Composable
private fun LogEntry(
    entry: IFileConverter.Entry,
    filter: String,
    darkTheme: Boolean,
    spanStyle: SpanStyle,
    style: LumberjackView.Style,
    useScrollableLines: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (style.useAlternatingRowColors) {
                    Modifier.background(if (entry.lineNumber % 2 == 0) style.color1 else style.color2)
                } else Modifier
            )
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
        if (useScrollableLines.value) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = INSET)
                    .horizontalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = INSET),
                style = MaterialTheme.typography.bodySmall
            )
        }

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

    data object Reload : Data() {
        override val count = 0
    }
}

@Throws(IOException::class)
private fun readLines(
    filePath: Path
): List<String> {
    val lines = ArrayList<String>()
    if (SystemFileSystem.exists(filePath)) {
        SystemFileSystem.source(filePath).buffered().use {
            while (true) {
                val line = it.readLine() ?: break
                lines += line
            }
        }
    }
    return lines
}
