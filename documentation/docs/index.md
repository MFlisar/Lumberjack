---
icon: material/information-variant
---

{% include 'header.md' %}

{% include 'description.md' %}

{% include 'region-features.md' %}

* simply lazy evalution based logger based on a `L` class for the shortest possible log codes
* easily extendible with custom loggers
* offers console and file logger implementations
* supports log filtering
* providers a viewer for log files

{% include 'screenshots.md' %}

{% include 'modules.md' %}

{% include 'platforms.md' %}

!!! note

    iOS would be supported theoretically, but currently I don't know iOS and don't own an apple device - the problem is only that I can't replace `ThreadLocal` and `StackTraceElement` in a non jvm environment. Additionally the console logging would need an iOS implementation as well. If you know how this can be done and want to contribute, that would be much appreciated.

* (1) notification and feedback module are android specific modules and therefor only support android
* (2) the viewer module is and older view based module that just supports android based on its nature
* (3) timber is only supported on android and therefor those modules are android only modules as well

{% include 'demo.md' %}

## :page_with_curl: Notes

#### Timber vs Lumberjack implementation

This library fully supports Jack Whartons [Timber](https://github.com/JakeWharton/timber) logging library (v4!). And was even based on it until Lumberjack v6. Beginning with v6 I wrote new modules that work without timber which leads to a smaller and more versitile non timber version. I would advice you to use the non timber versions but if you want to you can simply use the timber modules I provide as well - whatever you prefer.

??? info "Why did I do this?"

    I decided to not use `Timber` myself anymore because of following reasons:
    
    * `Timber` does explicitly rely on non lazy evaluating logging - it was a decision made by *Jack Wharton* and was the main reason to write `Lumberjack` at the beginning
    * `Timber` is restrictive regarding class extensions - in v5 I would need access to a field to continue supporting timber in `Lumberjack`
    * `Timber` is considered as working and feature requests and/or pull requests are not accepted if not really necessary - like e.g. my minimal one [here](https://github.com/JakeWharton/timber/issues/477).
    * additionally I always needed to extend the `BaseTree` from `Timber` because of the limiting restrictions of the default `BaseTree` as well as it was to restrictive to make adjustment in it ( I always had a nearly 1:1 copy of it inside my library [here](https://github.com/MFlisar/Lumberjack/blob/595d4de0ae76338e66cf42f7324f51c945699fa8/library/implementations/timber/src/main/java/timber/log/BaseTree.kt#L9){target=_blank}). This was needed to allow to adjust the stack trace depth so that `Lumberjack` will log the correct calling place as a wrapper around `Timber`.
    
    **This lead to my final decision**
    
    `Lumberjack` does not need `Timber` and I provide a way to plug in `Timber` into `Lumberjack` now - this way using `Timber` and `Lumberjack` in combination is possible but not necessary anymore.