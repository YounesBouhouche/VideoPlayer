package com.younesbouh.videoplayer.main.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun NavigationContainer(
    modifier: Modifier = Modifier,
    navigationBarVisible: Boolean = true,
    navigationBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val isCompact =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    if (isCompact)
        Column(modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize().weight(1f)) {
                content()
            }
            AnimatedVisibility(navigationBarVisible, Modifier.fillMaxWidth()) {
                navigationBar()
            }
        }
    else
        Row(modifier.fillMaxSize()) {
            AnimatedVisibility(navigationBarVisible, Modifier.fillMaxHeight()) {
                navigationBar()
            }
            Box(Modifier.fillMaxSize().displayCutoutPadding().weight(1f)) {
                content()
            }
        }
}