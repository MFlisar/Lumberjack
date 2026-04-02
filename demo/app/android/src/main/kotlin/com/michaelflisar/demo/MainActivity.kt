package com.michaelflisar.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import com.michaelflisar.lumberjack.demo.BuildKonfig
import com.michaelflisar.lumberjack.demo.DemoApp
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoApp(
                name = BuildKonfig.appName,
                platform = "Android",
                setup = App.setup,
                ioContext = Dispatchers.IO,
                sendFeedback = { receiver: String ->
                    // works on android and iOS only
                    L.sendFeedback(
                        config = FeedbackConfig.create(
                            receiver = receiver,
                            appName = BuildKonfig.appName,
                            appVersion = BuildKonfig.versionName
                        ),
                        attachments = listOfNotNull(App.setup.getLatestLogFilePath())
                    )
                }
            )
        }
    }
}
