## ComposeViewer Module

The `Storage` is an abstraction to support any storage implementation. The `storage-datastore` module provides an implementation based on the [Android JetPack DataStore](https://developer.android.com/topic/libraries/architecture/datastore).

This module is placed inside the `storage-datastore` artifact and can simply be used like following:

#### Android/iOS Implementation

```kotlin
object SettingsModel : SettingsModel(
    DataStoreStorage.create(
        name: String = "settings",
        encryption: StorageEncryption? = null,
        cache: Boolean = true
    )
) {
    // ...
}
```

#### JVM Implementation

```kotlin
object SettingsModel : SettingsModel(
    DataStoreStorage.create(
        folder: File,
        name: String = "settings",
        encryption: StorageEncryption? = null,
        cache: Boolean = true
    )
) {
    // ...
}
```
