package com.younesbouh.videoplayer.main.presentation.util

import kotlin.math.abs
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

val Long.timeString: String
    get() =
        String.format(
            "${if (this < 0) "-" else ""}%02d:%02d:%02d",
            (abs(this) / (1000 * 60 * 60)) % 24,
            (abs(this) / (1000 * 60)) % 60,
            (abs(this) / 1000) % 60,
        )

val Long.sizeString: String
    get() =
        if (this <= 0) "0B"
        else {
            val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
            val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
            "${DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
        }
