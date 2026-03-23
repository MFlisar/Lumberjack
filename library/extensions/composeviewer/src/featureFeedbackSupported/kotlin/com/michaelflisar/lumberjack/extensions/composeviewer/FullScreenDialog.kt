package com.michaelflisar.lumberjack.extensions.composeviewer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
fun FullScreenDialog(
    visible: MutableState<Boolean>,
    darkStatus: Boolean = MaterialTheme.colorScheme.primary.luminance() < .5f,
    darkNavigation: Boolean = MaterialTheme.colorScheme.background.luminance() < .5f,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = false
        ),
        onDismissRequest = { visible.value = false }
    ) {
        makeFullscreen(darkStatus, darkNavigation)
        content()
    }
}

@Composable
private fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
private fun getActivityWindow(): Window? = LocalView.current.context.getActivityWindow()

private tailrec fun Context.getActivityWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.getActivityWindow()
        else -> null
    }

@Composable
private fun makeFullscreen(
    darkStatus: Boolean,
    darkNavigation: Boolean
) {
    val activityWindow = getActivityWindow()
    val dialogWindow = getDialogWindow()
    val parentView = LocalView.current.parent as View

    SideEffect {
        if (activityWindow != null && dialogWindow != null) {
            // get and apply the activity attributes to the dialog parent view
            val attributes = WindowManager.LayoutParams()
            attributes.copyFrom(activityWindow.attributes)
            attributes.type = dialogWindow.attributes.type
            dialogWindow.attributes = attributes
            parentView.layoutParams = FrameLayout.LayoutParams(
                activityWindow.decorView.width,
                activityWindow.decorView.height
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dialogWindow.isNavigationBarContrastEnforced = true
            }
            WindowCompat.getInsetsController(dialogWindow, dialogWindow.decorView).apply {
                isAppearanceLightStatusBars = !darkStatus
                isAppearanceLightNavigationBars = !darkNavigation
            }
        }
    }
}