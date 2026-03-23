package com.michaelflisar.lumberjack.extensions.composeviewer

import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider

internal actual fun getFeedbackImpl(): IFeedbackProvider {
    return FeedbackImpl()
}