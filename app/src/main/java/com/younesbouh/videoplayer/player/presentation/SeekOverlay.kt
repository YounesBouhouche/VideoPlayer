package com.younesbouh.videoplayer.player.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.younesbouh.videoplayer.main.presentation.util.timeString
import kotlin.math.abs

@Composable
fun SeekOverlay(
    visible: Boolean,
    value: Long,
    modifier: Modifier = Modifier
) {
    Overlay(visible, modifier) {
        Text(
            "${if(value < 0) "-" else if (value == 0L) "" else "+"} ${abs(value).timeString}",
            Modifier.background(MaterialTheme.colorScheme.background.copy(alpha = .8f), RoundedCornerShape(100)).padding(18.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}