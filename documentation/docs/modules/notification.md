---
icon: material/puzzle
---

## Notification Extension

!!! note

    This extension does only support android!

This small extension provides you with with a few functions to create notifications (for app testers or for the dev for example) that can be clicked and then will allow the user to send the log file to you via the `extension-feedback`. Or to open the log file by clicking the notification.

```kotlin
// shows a notifcation - on notification click the suer can do following:
// * nothing
// * send a mail with optional attachments like e.g. log files, database, whatever
// * execute a custom action
fun L.showNotification(
    context: Context,
    notificationIcon: Int,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String = "Rare exception found",
    notificationText: String = "Please report this error by clicking this notification, thanks",
    clickHandler: NotificationClickHandler
)

// Click Handlers
// here's a short overview of the available click handlers
sealed class NotificationClickHandler {

    class SendFeedback(
        context: Context,
        val receiver: String,
        val subject: String = "Exception found in ${context.packageName}",
        val titleForChooser: String = "Send report with",
        val attachments: List<File> = emptyList()
    ) : NotificationClickHandler()

    class ClickIntent(
        val intent: Intent,
        val apply: ((builder: NotificationCompat.Builder) -> Unit)? = null
    ): NotificationClickHandler()

    data object None: NotificationClickHandler()
}	
```