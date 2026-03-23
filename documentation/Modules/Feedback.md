---
icon: material/puzzle
---

!!! note

    This extension does only support android!

This small extension simply allows you to send a log file via mail (no internet connection required). This will be done by sharing the file as email `Intent`.

```kotlin
val setup: FileLoggerSetup = ...
L.sendFeedback(
    config = FeedbackConfig.create(
        receiver = "mail@gamil.com",
        appName = "App Name",
        appVersion = "1.0.0"
    ),
    attachments = listOfNotNull(setup.getLatestLogFilePath())
)
```