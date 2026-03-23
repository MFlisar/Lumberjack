package com.michaelflisar.lumberjack.core

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val LoggingIOContext: CoroutineContext = Dispatchers.Default