package com.younesbouh.videoplayer.player.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Overlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    background: Brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)),
    content: @Composable (AnimatedVisibilityScope.() -> Unit)
) {
    AnimatedVisibility(visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
            content()
        }
    }
}