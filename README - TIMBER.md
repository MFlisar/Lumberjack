### Timber

I decided to not use `Timber` myself anymore because of following reasons:

* I never needed to add any external loggers besides the ones provided inside my `Lumberjack` library
* `Timber` does explicitly rely on non lazy evaluating logging - it was a decision made by *Jack Wharton* and was the main reason to write `Lumberjack` at the beginning
* `Timber` is restrictive regarding class extensions - in v5 I would need access to a field to continue supporting timber in `Lumberjack`

`Timber` is considered as working and feature requests and/or pull requests are not accepted - like e.g. my minimal one [here](https://github.com/JakeWharton/timber/issues/477).

Additionally I always needed to extend the `BaseTree` from `Timber` as it was to restrictive to make adjustment in it, I always had a nearly 1:1 copy of it inside my library here:

https://github.com/MFlisar/Lumberjack/blob/595d4de0ae76338e66cf42f7324f51c945699fa8/library/implementations/timber/src/main/java/timber/log/BaseTree.kt#L9

This was needed to allow to adjust the stack trace depth so that `Lumberjack` will log the correct calling place as a wrapper around `Timber`.

##### Final decision

I don't need `Timber` and I provide a way to plug in `Timber` into `Lumberjack` now - this way using `Timber` and `Lumberjack` in combination is possible but not necessary anymore.