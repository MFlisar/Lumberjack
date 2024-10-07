package com.michaelflisar.lumberjack.core.classes

private const val COLOR_BLUE = -16776961
private const val COLOR_RED = -65536
private const val COLOR_LIGHT_BLUE = -5383962//Color.rgb(173, 216, 230)
private const val COLOR_ORANGE = -23296//Color.rgb(255, 165, 0)
private const val COLOR_LIGHT_RED = -13109//Color.rgb(255, 204, 203)

enum class Level(
    val order: Int,
    val shortcut: String,
    val color: Int? = null,
    val colorDark: Int? = color
) {
    VERBOSE(1, "V"),
    DEBUG(2, "D"),
    INFO(3, "I", COLOR_BLUE, COLOR_LIGHT_BLUE),
    WARN(4, "W", COLOR_ORANGE),
    ERROR(5, "E", COLOR_RED, COLOR_LIGHT_RED),
    WTF(6, "WTF", COLOR_RED, COLOR_LIGHT_RED),
    NONE(-1, "-")
    ;
}