package com.michaelflisar.lumberjack.core.classes

private const val COLOR_BLUE = 255
private const val COLOR_RED = 16711680
private const val COLOR_LIGHT_BLUE = 11393254//Color.rgb(173, 216, 230)
private const val COLOR_ORANGE = 16753920//Color.rgb(255, 165, 0)
private const val COLOR_LIGHT_RED = 16764107//Color.rgb(255, 204, 203)

enum class Level(
    val shortcut: String,
    val color: Int? = null,
    val colorDark: Int? = color
) {
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I", COLOR_BLUE, COLOR_LIGHT_BLUE),
    WARN("W", COLOR_ORANGE),
    ERROR("E", COLOR_RED, COLOR_LIGHT_RED),
    WTF("WTF", COLOR_RED, COLOR_LIGHT_RED),
    NONE("-")
    ;
}