package com.michaelflisar.lumberjack.core

actual interface Parcelable : android.os.Parcelable
actual typealias Parcelize = kotlinx.parcelize.Parcelize
actual typealias IgnoredOnParcel = kotlinx.parcelize.IgnoredOnParcel