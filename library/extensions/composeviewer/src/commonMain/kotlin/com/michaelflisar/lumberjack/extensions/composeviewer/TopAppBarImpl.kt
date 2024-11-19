package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarImpl(
    title: String,
    setup: IFileLoggingSetup,
    state: LumberjackDialog.State,
    mail: String?
) {
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showMenu2 by remember { mutableStateOf(false) }
    val feedback by remember {
        derivedStateOf { getFeedbackImpl() }
    }
    feedback.Init()
    androidx.compose.material3.TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = {
                scope.launch {
                    state.listState.scrollToItem(Int.MAX_VALUE)
                }
            }) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
            IconButton(onClick = {
                scope.launch {
                    state.listState.scrollToItem(0)
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
                                state.logFileData.value = Data.Reload
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Log File(s)") },
                            leadingIcon = { Icon(Icons.Default.Delete, null) },
                            onClick = {
                                scope.launch {
                                    setup.clearLogFiles()
                                    state.logFileData.value = Data.Reload
                                    showMenu = false
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Select Log File") },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.InsertDriveFile,
                                    null
                                )
                            },
                            onClick = {
                                showMenu = false
                                showMenu2 = true
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Scrollable Lines") },
                            leadingIcon = {
                                Icon(
                                    if (state.useScrollableLines.value) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    null
                                )
                            },
                            onClick = {
                                state.useScrollableLines.value = !state.useScrollableLines.value
                            }
                        )
                        if (mail != null && feedback.supported()) {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Send Mail") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Mail,
                                        null
                                    )
                                },
                                onClick = {
                                    feedback.sendFeedback(
                                        receiver = mail,
                                        attachments = listOfNotNull(setup.getLatestLogFilePath())
                                    )
                                    showMenu2 = true
                                }
                            )
                        }
                    }
                }
                if (showMenu2) {
                    val files = setup.getAllExistingLogFilePaths()
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
                                        Icons.AutoMirrored.Filled.InsertDriveFile,
                                        null
                                    )
                                },
                                onClick = {
                                    state.logFile.value = file
                                    state.logFileData.value = Data.Reload
                                    showMenu2 = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}