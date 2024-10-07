## Feedback Extension

> [!NOTE]  
> This extension does only support android!

This small extension simply allows you to send a log file via mail (no internet connection required). This will be done by sharing the file as email `Intent`.

```kotlin
L.sendFeedback(
    context = context, 
    receiver = "some.mail@gmail.com",
    attachments = listOfNotNull(<file-logging-setup>.getLatestLogFiles())  
)
```