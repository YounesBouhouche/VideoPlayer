package com.younesbouh.videoplayer.player.presentation

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun VerticalLinearProgressIndicator(
    @FloatRange(from = 0.0, to = 1.0)
    progress: Float,
    icon: ImageVector,
    trackWidth: Dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = ""
    )
    val density = LocalDensity.current
    var height by remember { mutableStateOf(0.dp) }
    val trackHeight by remember {
        derivedStateOf {
            with(density) {
                (animatedProgress * (height - trackWidth).toPx()).roundToInt().toDp() + trackWidth
            }
        }
    }
    Column(
        modifier
            .width(trackWidth + 16.dp)
            .clip(RoundedCornerShape(100))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f)
                .onGloballyPositioned {
                    with(density) {
                        height = it.size.height.toDp()
                    }
                }
                .onSizeChanged {
                    with(density) {
                        height = it.height.toDp()
                    }
                }
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                Modifier
                    .width(trackWidth)
                    .height(trackHeight)
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(icon, null,
                    Modifier
                        .align(Alignment.TopCenter)
                        .size(trackWidth)
                        .padding(10.dp))
            }
        }
        Text("${(progress * 100).roundToInt()}", Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.primary)
    }
}