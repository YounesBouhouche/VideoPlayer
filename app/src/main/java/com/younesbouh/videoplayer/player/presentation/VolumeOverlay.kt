package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BoxScope.VolumeOverlay(
    visible: Boolean,
    value: Float,
    modifier: Modifier = Modifier
) = ProgressOverlay(
    visible,
    value,
    when {
        value == 0f -> Icons.AutoMirrored.Default.VolumeMute
        value <= .5f -> Icons.AutoMirrored.Default.VolumeDown
        else -> Icons.AutoMirrored.Default.VolumeUp
    },
    Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surface,
            Color.Transparent
        )
    ),
    modifier
        .fillMaxHeight()
        .fillMaxWidth(.5f)
)