package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.ProgressOverlay(
    visible: Boolean,
    value: Float,
    icon: ImageVector,
    background: Brush = Brush.horizontalGradient(),
    modifier: Modifier = Modifier
) {
    Overlay(visible, modifier, background) {
        VerticalLinearProgressIndicator(value, icon, 42.dp, Modifier.height(300.dp))
    }
}