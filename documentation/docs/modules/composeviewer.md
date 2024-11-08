---
icon: material/puzzle
---

!!! info

    If you use compose in your app you should use this viewer - it allows you to show log files directly inside your app.

```kotlin
val showLogViewer = rememberSaveable {
    mutableStateOf(false)
}
LumberjackDialog(
    visible = showLogViewer,
    title = "Logs",
    setup = <a file logging setup>,
    // optional
    style = LumberjackViewDefaults.style(),
    darkTheme = isSystemInDarkTheme(),
    mail = null
)
```

Alternatively you can always embed the view directly like following:

```kotlin
LumberjackView(
    setup = <a file logging setup>,
    // optional
    modifier = Modifier,
    file = rememberLogFile(),
    data = rememberLogFileData(),
    state = rememberLazyListState(),
    darkTheme = isSystemInDarkTheme(),
    style = LumberjackViewDefaults.style(),
    useScrollableLines = remember { mutableStateOf(false) }
)
```


!!! tip

    You can also embed the whole dialog "content view" inside a layout if you want to also show the menu and use it's features

```kotlin
LumberjackDialogContent(
    title = "Logs",
    setup = <a file logging setup>,
    // optional
    style= LumberjackViewDefaults.style(),
    darkTheme = isSystemInDarkTheme(),
    mail = null
)
```

| Compose Viewer | |
|-|-|
| ![Viewer](https://raw.githubusercontent.com/MFlisar/Lumberjack/refs/heads/master/screenshots/compose-viewer1.jpg) | ![Viewer](https://raw.githubusercontent.com/MFlisar/Lumberjack/refs/heads/master/screenshots/compose-viewer2.jpg) |