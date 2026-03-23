package com.michaelflisar.lumberjack.core

import kotlin.coroutines.CoroutineContext

@RequiresOptIn
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class InternalApi

@InternalApi
expect val LoggingIOContext: CoroutineContext