package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BoxScope.BrightnessOverlay(
    visible: Boolean,
    value: Float,
    modifier: Modifier = Modifier
) = ProgressOverlay(
    visible,
    value,
    when {
        value <= 1/3f -> Icons.Default.BrightnessLow
        value <= 2/3f -> Icons.Default.BrightnessMedium
        else -> Icons.Default.BrightnessHigh
    },
    Brush.horizontalGradient(
        listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.surface
        )
    ),
    modifier
        .fillMaxHeight()
        .fillMaxWidth(.5f)
)