package com.younesbouh.videoplayer.player.presentation

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.WidthFull
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM

data class ResizeMode(
    val mode: Int,
    val text: String,
    val icon: ImageVector
)

@SuppressLint("UnsafeOptInUsageError")
val resizeModes = listOf(
    ResizeMode(RESIZE_MODE_FIT, "Fit", Icons.Default.FitScreen),
    ResizeMode(RESIZE_MODE_FILL, "Fill", Icons.Default.Fullscreen),
    ResizeMode(RESIZE_MODE_ZOOM, "Zoom", Icons.Default.ZoomOutMap),
    ResizeMode(RESIZE_MODE_FIXED_WIDTH, "Fixed Width", Icons.Default.WidthFull),
    ResizeMode(RESIZE_MODE_FIXED_HEIGHT, "Fixed Height", Icons.Default.Height)
)