package com.younesbouh.videoplayer.settings.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import com.younesbouh.videoplayer.R

enum class Theme(val label: Int, val icon: ImageVector) {
    LIGHT(R.string.light, Icons.Default.LightMode),
    SYSTEM(R.string.default_theme, Icons.Default.BrightnessAuto),
    DARK(R.string.dark, Icons.Default.DarkMode);

    companion object {
        fun fromString(value: String): Theme =
            Theme.entries.find { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}