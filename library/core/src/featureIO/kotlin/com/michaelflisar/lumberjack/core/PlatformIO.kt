package com.michaelflisar.lumberjack.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

actual val LoggingIOContext: CoroutineContext = Dispatchers.IO