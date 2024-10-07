## ComposeViewer Extension

This small extension simply allows you to send a log file via mail (no internet connection required). This will be done by sharing the file as email `Intent`.

If you use compose in your app you should use this viewer - it allows you to show log files directly inside your app.

```kotlin
val showLogViewer = rememberSaveable {
    mutableStateOf(false)
}
LumberjackDialog(
    visible = showLogViewer,
    title = "Logs",
    setup = <a file logging setup>,
    mail = "some.mail@gmail.com"
)
```

| Compose Viewer | |
|-|-|
| ![Viewer]({{ page.meta.screenshots }}/compose-viewer1.jpg) | ![Viewer]({{ page.meta.screenshots }}/compose-viewer2.jpg) |